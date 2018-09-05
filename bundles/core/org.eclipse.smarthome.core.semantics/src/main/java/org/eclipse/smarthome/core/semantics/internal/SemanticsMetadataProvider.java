/**
 * Copyright (c) 2014,2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.smarthome.core.semantics.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.common.registry.ProviderChangeListener;
import org.eclipse.smarthome.core.items.GroupItem;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.items.Metadata;
import org.eclipse.smarthome.core.items.MetadataKey;
import org.eclipse.smarthome.core.items.MetadataProvider;
import org.eclipse.smarthome.core.semantics.model.Point;
import org.eclipse.smarthome.core.semantics.model.Property;
import org.eclipse.smarthome.core.semantics.model.TagSet;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@NonNullByDefault
@Component
public class SemanticsMetadataProvider implements MetadataProvider {

    private final Set<Metadata> semantics = new TreeSet<>();

    @NonNullByDefault({})
    private ItemRegistry itemRegistry;

    @Activate
    protected void activate() {
        processItems();
    }

    protected void deactivate() {

    }

    @Reference
    protected void setItemRegistry(ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    protected void unsetItemRegistry(ItemRegistry itemRegistry) {
        this.itemRegistry = null;
    }

    @Override
    public @NonNull Collection<@NonNull Metadata> getAll() {
        return semantics;
    }

    @Override
    public void addProviderChangeListener(@NonNull ProviderChangeListener<@NonNull Metadata> listener) {
    }

    @Override
    public void removeProviderChangeListener(@NonNull ProviderChangeListener<@NonNull Metadata> listener) {
    }

    private void processItems() {
        for (Item item : itemRegistry.getAll()) {
            MetadataKey key = new MetadataKey("semantics", item.getName());
            Map<String, Object> configuration = new HashMap<>();
            Set<String> tags = item.getTags();
            Class<? extends TagSet> type = processType(tags, item, configuration);
            if (type == Point.class) {
                processProperty(tags, configuration);
            }
            Metadata md = new Metadata(key, TagSet.name(), configuration);
            semantics.add(md);
        }
    }

    private void processProperty(Set<String> tags, Map<String, Object> configuration) {
        for (String tag : tags) {
        }
    }

    private Class<? extends TagSet> processType(Set<String> tags, Item item, Map<String, Object> configuration) {
        if (tags.size() > 0) {
            String type = tags.iterator().next();
            for (String parent : item.getGroupNames()) {
                Item parentItem = itemRegistry.get(parent);
                if (parentItem instanceof GroupItem && "Location".equals(getSemanticType(parentItem))) {
                    configuration.put("hasLocation", parentItem.getName());
                }
            }
        }
        return Point.class;
    }

    private void processPropertyTag(Set<String> tags, Map<String, Object> configuration) {
        if (tags.size() > 1) {
            for (String tag : tags) {
                try {
                    Property property = Property.valueOf(tag);
                    configuration.put("hasProperty", property);
                    return;
                } catch (IllegalArgumentException e) {
                }
            }
        }
    }

    private @Nullable String getSemanticType(Item item) {
        Set<String> tags = item.getTags();
        if (tags.size() > 0) {
            return tags.iterator().next();
        }
        return null;

    }

}
