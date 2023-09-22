/*
 * This file is part of Kiama.
 *
 * Copyright (C) 2014-2021 Anthony M Sloane, Macquarie University.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.bitbucket.inkytonik.kiama
package util

/**
 * An entity that represents some program object.
 */
abstract class Entity {

    /**
     * Does this entity represent an error state or not? Default:
     * no.
     */
    def isError : Boolean =
        false

}
