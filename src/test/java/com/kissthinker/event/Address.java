package com.kissthinker.event;

import java.io.Serializable;

/**
 * @author David Ainslie
 */
public class Address implements Serializable
{
    /** */
    private static final long serialVersionUID = 1L;

    /** */
    private String street;
    
    /** */
    private String postCode;

    /**
     *
     */
    public Address()
    {
    }

    /**
     *
     * @param street
     * @param postCode
     */
    public Address(String street, String postCode)
    {
        this.street = street;
        this.postCode = postCode;
    }


    /**
     *
     * @return
     */
    public String street()
    {
        return street;
    }


    /**
     *
     * @param street
     */
    public void street(String street)
    {
        this.street = street;
    }

    /**
     *
     * @return
     */
    public String postCode()
    {
        return postCode;
    }

    /**
     *
     * @param postCode
     */
    public void postCode(String postCode)
    {
        this.postCode = postCode;
    }
}