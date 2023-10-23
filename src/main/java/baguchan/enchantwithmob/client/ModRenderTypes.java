package baguchan.enchantwithmob.client;

import net.minecraft.client.renderer.RenderStateShard;

public class ModRenderTypes {
    public static final RenderStateShard.ShaderStateShard RENDERTYPE_ENCHANT_BEAM_SHADER = new RenderStateShard.ShaderStateShard(ModShaders::getRenderTypeEnchantBeamShader);

}
