/*
 * Copyright 2007-2011 Tom Castle & Lawrence Beadle
 * Licensed under GNU Lesser General Public License
 * 
 * This file is part of EpochX: genetic programming software for research
 * 
 * EpochX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EpochX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with EpochX. If not, see <http://www.gnu.org/licenses/>.
 * 
 * The latest version is available from: http://www.epochx.org
 */
package org.epochx.op;

import org.epochx.core.Model;
import org.epochx.life.*;

/**
 * Abstract class that should be extended to support auto-configuring operators.
 */
public abstract class ConfigOperator<T extends Model> implements Operator, ConfigListener {

	// The controlling model.
	private T model;

	/**
	 * Constructs a <code>ConfigOperator</code> with a model.
	 * 
	 * @param model the Model from which configuration parameters should be
	 *        loaded. If the model is non-null then this operator will be
	 *        enrolled to
	 *        receive configure events.
	 */
	public ConfigOperator(final T model) {
		this.model = model;

		updateListener();
	}

	/**
	 * Configures the operator with parameters from the model. This method will
	 * be called at the appropriate times to configure this component.
	 * Implementing classes should override this method to setup the operator
	 * with any parameters required from the model. If no model is set then this
	 * instance will not be subscribed to configure events and so alternative
	 * means must be used to configure it.
	 */
	@Override
	public void onConfigure() {
	}

	/**
	 * Returns the model that is providing the configuration for this
	 * initialiser, or <code>null</code> if none is set.
	 * 
	 * @return the model that is supplying the configuration parameters or null
	 *         if the parameters are individually set.
	 */
	public T getModel() {
		return model;
	}

	/**
	 * Sets a model that will provide the configuration for this operator.
	 * The necessary parameters will be obtained from the model the next time,
	 * and each time a configure event is triggered. Note that until a configure
	 * event is fired this operator may be in an unusable state. Any
	 * previously set parameters will stay active until they are overwritten at
	 * the next configure event.
	 * 
	 * <p>
	 * If a model is already set, it may be cleared by calling this method with
	 * <code>null</code>.
	 * 
	 * @param model the model to set or null to clear any current model.
	 */
	public void setModel(final T model) {
		this.model = model;

		updateListener();
	}

	/*
	 * Adds or removes self from configure events.
	 */
	private void updateListener() {
		// Ensure config listeners are kept up to date.
		if (model == null) {
			Life.get().removeConfigListener(this);
		} else {
			Life.get().addConfigListener(this, false);
		}
	}
}
