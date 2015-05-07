package org.dodgyjammers.jammypiece.infra;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

@Plugin(name = "WsLog", category = "Core", elementType = "appender", printObject = true)
public final class WsLogAppender extends AbstractOutputStreamAppender<WsLogManager> {
 
    private WsLogAppender(String name, Layout layout, Filter filter, WsLogManager manager,
                         boolean ignoreExceptions) {
      super(name, layout, filter, ignoreExceptions, true, manager);
    }
 
    @PluginFactory
    public static WsLogAppender createAppender(@PluginAttribute("name") String name,
                                              @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
                                              @PluginElement("Layout") Layout layout,
                                              @PluginElement("Filters") Filter filter) {
 
        if (name == null) {
            LOGGER.error("No name provided for StubAppender");
            return null;
        }
 
        WsLogManager manager = WsLogManager.getWsLogManager(name, layout);
        if (manager == null) {
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new WsLogAppender(name, layout, filter, manager, ignoreExceptions);
    }
 }
