/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.runtime.truffle.runtime.primitives;

import raw.sources.CacheStrategy;
import raw.sources.LocationDescription;
import raw.sources.LocationSettingKey;
import raw.sources.LocationSettingValue;
import scala.collection.GenMap;
import scala.collection.immutable.Map;
import scala.collection.immutable.HashMap;

public class LocationObject {
    private final LocationDescription locationDescription;

    public LocationObject(String url) {
        this.locationDescription = new LocationDescription(url, new HashMap<>(), CacheStrategy.NoCache());
    }

    public LocationObject(String url, Map<LocationSettingKey, LocationSettingValue> params) {
        this.locationDescription = new LocationDescription(url, params, CacheStrategy.NoCache());
    }

    public LocationObject(String url, Map<LocationSettingKey, LocationSettingValue> params, CacheStrategy cacheStrategy) {
        this.locationDescription = new LocationDescription(url, params, cacheStrategy);
    }

    public LocationObject(LocationDescription locationDescription) {
        this.locationDescription = locationDescription;
    }

    public LocationDescription getLocationDescription() {
        return locationDescription;
    }

//    public String getUrl() {
//        return locationDescription.url();
//    }

}
