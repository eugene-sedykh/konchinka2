package com.jjjackson.konchinka.util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

public class ActorHelper {

    public static Group getLayerByName(Array<Actor> actors, String layerName) {
        for (Actor actor : actors) {
            if (actor instanceof Group) {
                if (layerName.equals(actor.getName())) {
                    return (Group) actor;
                } else {
                    Group group = getLayerByName(((Group) actor).getChildren(), layerName);
                    if (group != null) return group;
                }
            }
        }
        return null;
    }
}
