/*
 * Copyright (C) 2011-2012 AlarmApp.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.helpme.demo.utils.position;

import java.util.Date;

import android.location.Location;

public abstract class SimpleSelectionStrategy {

	public static boolean isPositionRelevant(Location location) {
		Date now = new Date();

		long ageInMs = now.getTime() - location.getTime();

		if (ageInMs > 2 * 60 * 1000) {
			return false;
		}

		return true;
	}

	public static boolean isPositionRelevant(Location lastLocation, Location currentLocation) {

		if (hasMovedUnder100m(lastLocation, currentLocation)) {
			return false;
		}

		if (hasTurnedUnder10Degree(lastLocation, currentLocation)) {
			return false;
		}

		return true;
	}
	
	public static boolean isPositionRelevant(Position position) {
		Date now = new Date();

		long ageInMs = now.getTime() - position.getMeasureDateTime();

		if (ageInMs > 2 * 60 * 1000) {
			return false;
		}

		return true;
	}
	
	public static boolean isPositionRelevant(Position lastPosition, Position currentPosition){
		if (hasMovedUnder100m(lastPosition, currentPosition)) {
			return false;
		}

		if (hasTurnedUnder10Degree(lastPosition, currentPosition)) {
			return false;
		}

		return true;
	}
	
	private static boolean hasMovedUnder100m(Position lastPosition, Position currentPosition) {
		return Math.abs(lastPosition.calculateSphereDistance(currentPosition)) < 10;
	}
	
	private static boolean hasTurnedUnder10Degree(Position lastPosition, Position currentPosition) {
		return Math.abs(lastPosition.getDirection() - currentPosition.getDirection()) < 10;
	}

	private static boolean hasMovedUnder100m(Location lastLocation, Location currentLocation) {
		return Math.abs(lastLocation.distanceTo(currentLocation)) < 100;
	}

	private static boolean hasTurnedUnder10Degree(Location lastLocation, Location currentLocation) {
		return Math.abs(lastLocation.getBearing() - currentLocation.getBearing()) < 10;
	}
}
