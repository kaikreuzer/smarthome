package org.eclipse.smarthome.core.semantics;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.smarthome.core.semantics.model.Equipment;
import org.eclipse.smarthome.core.semantics.model.Location;
import org.eclipse.smarthome.core.semantics.model.Point;
import org.eclipse.smarthome.core.semantics.model.Tag;
import org.eclipse.smarthome.core.semantics.model.location.Room;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true)
public class TagService {

    private final Set<Class<? extends Equipment>> equipments = new HashSet<>();
    private final Set<Class<? extends Location>> locations = new HashSet<>();
    private final Set<Class<? extends Point>> points = new HashSet<>();

    protected void activate() {
        locations.add(Room.class);
        System.out.println(locations.iterator().next().getAnnotation(Tag.class).id());
    }

    // public TagSet byTagSet(String tagSet) {
    //
    // }
}
