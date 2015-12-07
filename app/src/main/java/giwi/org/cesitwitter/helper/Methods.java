/*
 * __________________
 * Qaobee
 * __________________
 *
 * Copyright (c) 2015.  Qaobee
 * All Rights Reserved.
 *
 * NOTICE: All information contained here is, and remains
 * the property of Qaobee and its suppliers,
 * if any. The intellectual and technical concepts contained
 * here are proprietary to Qaobee and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Qaobee.
 */

package giwi.org.cesitwitter.helper;


/**
 * The enum Methods.
 */
public enum Methods {
    PUT("PUT"),
    POST("POST"),
    GET("GET"),
    DELETE("DELETE");

    private String name;

    /**
     * Instantiates a new Methods.
     *
     * @param name the name
     */
    Methods(String name) {
        this.name = name;
    }
}
