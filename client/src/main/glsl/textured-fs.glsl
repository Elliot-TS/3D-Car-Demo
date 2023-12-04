#version 300 es
#define NUM_LIGHTS 5

precision highp float;

in vec4 texCoord;
in vec3 worldNormal;
in vec4 worldPosition;
in vec4 modelPosition;

uniform struct{
    vec3 position;
    mat4 viewProjMatrix; 
} camera;

uniform struct {
    sampler2D colorTexture; 
    samplerCube envTexture;
    float reflective;
} material;

uniform struct {
    vec4 position;
    vec4 direction;
    vec4 powerDensity;
} lights[NUM_LIGHTS];

out vec4 fragmentColor;

vec3 noiseGrad(vec3 r) {
    uvec3 s = uvec3(
            0x1D4E1D4E,
            0x58F958F9,
            0x129F129F);
    vec3 f = vec3(0, 0, 0);
    for(int i=0; i<16; i++) {
        vec3 sf =
            vec3(s & uvec3(0xFFFF))
            / 65536.0 - vec3(0.5, 0.5, 0.5);

        f += cos(dot(sf, r)) * sf;
        s = s >> 1;
    }
    return f;
}

void main(void) {
    vec3 viewDir = normalize(camera.position - worldPosition.xyz);
    vec3 normal = normalize(worldNormal);
    //normal += noiseGrad(modelPosition.xyz * 50.0) * 0.05;
    //normal = normalize(normal);
    //vec3 reflDir = reflect(-viewDir, normal);
    //fragmentColor = texture(material.envTexture, reflDir);

    if (material.reflective == 1.f) {
        vec3 reflDir = reflect(-viewDir, normal);
        fragmentColor = texture(material.envTexture, reflDir);
    }
    else {
        vec3 radiance = vec3(0, 0, 0);

        for(int iLight=0; iLight<NUM_LIGHTS; iLight++){
            // Environment (pos.xyzw = 0)
            //  lightDiff = vec3(1f,1f,1f);
            //  lightDir = normal;
            // Directional (pos.w=0):
            //  lightDiff = lights[iLight].position.xyz;
            //  lightDir = lights[iLight].direction.xyz;
            // Spot (pos.w=1, dir.w != 0):
            //  lightDiff = lights[iLight].position.xyz - worldPosition.xyz;
            //  lightDir = normalize(lightDiff);
            // Point (pos.w=1, dir.w = 0):
            //  lightDiff = lights[iLight].position.xyz - worldPosition.xyz;
            //  lightDir = normalize(lightDiff);

            bool isEnvironmentLight = lights[iLight].position == vec4(0,0,0,0);
            bool isDirectionalLight = lights[iLight].position.w == 0.0f;
            bool isSpotLight = (!isDirectionalLight) && (lights[iLight].direction.w != 0.0f);

            vec3 lightDiff = 
                lights[iLight].position.xyz - 
                worldPosition.xyz * lights[iLight].position.w;

            float distanceSquared = 
                isDirectionalLight ?
                1.0f :
                dot(lightDiff, lightDiff);

            vec3 lightDir =
                isEnvironmentLight ? // If environment lighting
                normal : // Use the normal as the light direction
                isDirectionalLight ? // If directional light
                lights[iLight].direction.xyz :  // Use direction
                normalize(lightDiff); // If spotlight, use lightDiff

            vec3 powerDensity = 
                lights[iLight].powerDensity.xyz
                / distanceSquared;

            if (
                    isSpotLight
                    && dot(-lightDir, lights[iLight].direction.xyz) <= cos(lights[iLight].direction.w)
               ) {
                powerDensity *= 0.f;
            }


            float cosa = clamp(
                    dot(lightDir, normal),
                    0.0,
                    1.0);
            radiance += 
                powerDensity * // M
                cosa * // n*l
                texture(material.colorTexture, texCoord.xy).rgb; // BRDF
        }
        //fragmentColor = vec4(abs(worldPosition.xyz/100.f), 1);
        fragmentColor = vec4(radiance, 1);
        //fragmentColor = vec4(normal, 1);
    }
}
