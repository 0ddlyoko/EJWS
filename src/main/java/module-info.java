import me.oddlyoko.ejws.base.BaseModule;
import me.oddlyoko.ejws.module.Module;

module EJWS {
    requires com.google.gson;
    requires transitive org.apache.logging.log4j;

    opens me.oddlyoko.ejws.module to com.google.gson;

    uses Module;

    exports me.oddlyoko.ejws.algo.dependencygraph;
    exports me.oddlyoko.ejws.base.events;
    exports me.oddlyoko.ejws.base.exceptions;
    exports me.oddlyoko.ejws.event;
    exports me.oddlyoko.ejws.module;
    exports me.oddlyoko.ejws.util;
    exports me.oddlyoko.ejws;

    provides Module with BaseModule;
}
