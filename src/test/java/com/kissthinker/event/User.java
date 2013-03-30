package com.kissthinker.event;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author David Ainslie
 */
public class User implements Serializable
{
    /** */
    public enum Gender {male, female}

    /** */
    private static final long serialVersionUID = 1L;

    /** */
    private transient String name;
    
    /** */
    private Gender gender;
    
    /** */
    private boolean pensioner;
    
    /** */
    private Address address;
    
    /** */
    private BigDecimal netWorth;

    /**
     *
     * @param name
     * @param gender
     * @param pensioner
     */
    public User(String name, Gender gender, boolean pensioner)
    {
        this.name = name;
        this.gender = gender;
        this.pensioner = pensioner;
    }

    /**
     *
     * @return
     */
    public String name()
    {
        return name;
    }

    /**
     *
     * @param name
     */
    public void name(String name)
    {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public Gender gender()
    {
        return gender;
    }

    /**
     *
     * @param gender
     */
    public void gender(Gender gender)
    {
        this.gender = gender;
    }

    /**
     *
     * @return
     */
    public boolean pensioner()
    {
        return pensioner;
    }


    /**
     *
     * @param pensioner
     */
    public void pensioner(boolean pensioner)
    {
        this.pensioner = pensioner;
    }

    /**
     *
     * @return
     */
    public Address address()
    {
        return address == null ? address = new Address() : address;
    }

    /**
     *
     * @param address
     */
    public void address(Address address)
    {
        this.address = address;
    }

    /**
     *
     * @return
     */
    public BigDecimal netWorth()
    {
        return netWorth;
    }

    /**
     *
     * @param netWorth
     */
    public void netWorth(BigDecimal netWorth)
    {
        this.netWorth = netWorth;
    }

    /**
     *
     * @author David Ainslie
     */
    public static class Event
    {
    }
}