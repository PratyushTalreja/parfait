package io.pcp.parfait;

import com.fasterxml.jackson.databind.JsonNode;

import systems.uom.quantity.Information;
import tec.uom.se.unit.MetricPrefix;
import tec.uom.se.unit.Units;
import static tec.uom.se.AbstractUnit.ONE;

import javax.measure.Unit;
import javax.measure.quantity.Time;

public class Specification {
    public String name;
    public String description;
    public Unit<?> unit = ONE;
    public ValueSemantics semantics = ValueSemantics.FREE_RUNNING;
    public String mBeanName;
    public String mBeanAttributeName;
    public String mBeanCompositeDataItem;
    
    public Specification() {
    }

	private Specification(String name, String description,
                String semantics, String unitName, String mBeanName,
                String mBeanAttributeName, String mBeanCompositeDataItem) {
        if (!name.isEmpty()) {
            this.name = name;
        }
        if (!description.isEmpty()) {
            this.description = description;
        }
        if (!semantics.isEmpty()) {
            if (semantics.equalsIgnoreCase("constant")) {
                this.semantics = ValueSemantics.CONSTANT;
            }
            else if (semantics.equalsIgnoreCase("counter")) {
                this.semantics = ValueSemantics.FREE_RUNNING;
            }
            else {
                this.semantics = ValueSemantics.MONOTONICALLY_INCREASING;
            }
        }
        if (unitName.equalsIgnoreCase("milliseconds")) {
        	Unit<Time> MILLISECONDS = MetricPrefix.MILLI(Units.SECOND);
			this.unit = MILLISECONDS;
        }
        if(unitName.equalsIgnoreCase("bytes")) {
        	Unit<Information> BYTE = systems.uom.unicode.CLDR.BYTE;
        	this.unit = BYTE;
        }
        this.mBeanName = mBeanName;
        if (!mBeanAttributeName.isEmpty()) {
            this.mBeanAttributeName = mBeanAttributeName;
        }
        if (!mBeanCompositeDataItem.isEmpty()) {
            this.mBeanCompositeDataItem = mBeanCompositeDataItem;
        }
    }

    public Specification(JsonNode node) {
        this(node.path("name").asText(),
             node.path("description").asText(),
             node.path("semantics").asText(),
             node.path("units").asText(),
             node.path("mBeanName").asText(),
             node.path("mBeanAttributeName").asText(),
             node.path("mBeanCompositeDataItem").asText());
    }

    public ValueSemantics getSemantics() {
        return semantics;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Unit<?> getUnits() {
        return unit;
    }

    public String getMBeanName() {
        return mBeanName;
    }

    public String getMBeanAttributeName() {
        return mBeanAttributeName;
    }

    public String getMBeanCompositeDataItem() {
        return mBeanCompositeDataItem;
    }
}