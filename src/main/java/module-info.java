module EJWS {
    requires com.google.gson;
    requires transitive org.apache.logging.log4j;
    requires static org.jetbrains.annotations;

    opens me.oddlyoko.ejws.module to com.google.gson;

    uses me.oddlyoko.ejws.module.Module;

    exports me.oddlyoko.ejws.event;
    exports me.oddlyoko.ejws.events;
    exports me.oddlyoko.ejws.exceptions;
    exports me.oddlyoko.ejws.module;
    exports me.oddlyoko.ejws.util;
    exports me.oddlyoko.ejws;
}
