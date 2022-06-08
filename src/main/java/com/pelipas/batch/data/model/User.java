/**
 * Genaro Pelipas (c) 2020
 */
package com.pelipas.batch.data.model;

import java.io.Serializable;

import lombok.Data;

/**
 * [desc]
 * 
 * @author gpelipas
 *
 */
@Data
public class User implements Serializable {

	private static final long serialVersionUID = 178763445558437845L;

	private String firstName;
	private String lastName;
	private String email;

}
