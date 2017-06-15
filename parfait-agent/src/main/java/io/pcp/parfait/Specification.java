package io.pcp.parfait;

import javax.management.ObjectName;
import javax.measure.Unit;
import static tec.uom.se.AbstractUnit.ONE;

public class Specification {
	String name, description, mBeanAttributeName, mBeanCompositeDataItem;
	ObjectName mBeanName;
	private Unit<?> units = ONE;
	private ValueSemantics semantics = ValueSemantics.FREE_RUNNING;

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
		return units;
	}

	public void setUnits(Unit<?> units) {
		this.units = units;
	}

	public ObjectName getmBeanName() {
		return mBeanName;
	}

	public void setmBeanName(ObjectName mBeanName) {
		this.mBeanName = mBeanName;
	}

	public String getmBeanAttributeName() {
		return mBeanAttributeName;
	}

	public void setmBeanAttributeName(String mBeanAttributeName) {
		this.mBeanAttributeName = mBeanAttributeName;
	}

	public String getmBeanCompositeDataItem() {
		return mBeanCompositeDataItem;
	}

	public void setmBeanCompositeDataItem(String mBeanCompositeDataItem) {
		this.mBeanCompositeDataItem = mBeanCompositeDataItem;
	}
	public <T> Monitorable<?> createMonitorable() {
		Monitorable<?> monitorable = new Monitorable<T>() {

			@Override
			public String getName() {
				return getName();
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return getDescription();
			}

			@Override
			public Unit<?> getUnit() {
				// TODO Auto-generated method stub
				return getUnits();
			}

			@Override
			public ValueSemantics getSemantics() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Class<T> getType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public T get() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void attachMonitor(Monitor m) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void removeMonitor(Monitor m) {
				// TODO Auto-generated method stub
				
			}
		};
		return monitorable;
	}
}
