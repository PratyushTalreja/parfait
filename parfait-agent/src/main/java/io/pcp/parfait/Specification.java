package io.pcp.parfait;

import com.fasterxml.jackson.databind.JsonNode;


import javax.measure.Unit;
import javax.measure.quantity.Time;
import javax.measure.unit.NonSI;

import static tec.uom.se.AbstractUnit.ONE;

import java.util.concurrent.TimeUnit;

public class Specification {
    private String name;
    private String description;
    private Unit<?> unit = ONE;
    private ValueSemantics semantics = ValueSemantics.FREE_RUNNING;
    private String mBeanName;
    private String mBeanAttributeName;
    private String mBeanCompositeDataItem;

    @SuppressWarnings("unchecked")
	public Specification(String name, String description,
                String semantics, String unitName, String mBeanName,
                String mBeanAttributeName, String mBeanCompositeDataItem) {
        if (!name.isEmpty())
            setName(name);
        if (!description.isEmpty())
            setDescription(description);
        if (!semantics.isEmpty()) {
            if (semantics.equalsIgnoreCase("constant"))
                setSemantics(ValueSemantics.CONSTANT);
            else if (semantics.equalsIgnoreCase("counter"))
                setSemantics(ValueSemantics.FREE_RUNNING);
            else
                setSemantics(ValueSemantics.MONOTONICALLY_INCREASING);
        }
        if (unitName.equalsIgnoreCase("milliseconds"))
        {
			Unit<Time> MILLISECONDS = (Unit<Time>) TimeUnit.MILLISECONDS;
            setUnits(MILLISECONDS);
        }
        else if(unitName.equalsIgnoreCase("bytes"))
        {
        	Unit<?> BYTE = (Unit<?>) NonSI.BYTE;
        	setUnits(BYTE);
        }
        else
        	setUnits(unit);
        setMBeanName(mBeanName);
        if (!mBeanAttributeName.isEmpty())
            setMBeanAttributeName(mBeanAttributeName);
        if (!mBeanCompositeDataItem.isEmpty())
            setMBeanCompositeDataItem(mBeanCompositeDataItem);
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

    public void setSemantics(ValueSemantics semantics) {
        this.semantics = semantics;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Unit<?> getUnits() {
        return unit;
    }

    public void setUnits(Unit<?> units) {
        this.unit = units;
    }

    public String getMBeanName() {
        return mBeanName;
    }

    public void setMBeanName(String mBeanName) {
        this.mBeanName = mBeanName;
    }

    public String getMBeanAttributeName() {
        return mBeanAttributeName;
    }

    public void setMBeanAttributeName(String mBeanAttributeName) {
        this.mBeanAttributeName = mBeanAttributeName;
    }

    public String getMBeanCompositeDataItem() {
        return mBeanCompositeDataItem;
    }

    public void setMBeanCompositeDataItem(String mBeanCompositeDataItem) {
        this.mBeanCompositeDataItem = mBeanCompositeDataItem;
    }
}