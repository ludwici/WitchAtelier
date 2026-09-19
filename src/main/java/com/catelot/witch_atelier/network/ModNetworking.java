package com.catelot.witch_atelier.network;

import com.catelot.witch_atelier.WitchAtelier;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";

    private ModNetworking() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToServer(WitchNetwork.TYPE, WitchNetwork.CODEC, WitchNetwork::handle);
        registrar.playToServer(NotebookPageNetwork.TYPE, NotebookPageNetwork.CODEC, NotebookPageNetwork::handle);
        WitchAtelier.LOGGER.debug("Registered Witch Atelier network payloads");
    }
}
