/*
 * File:                OnlinePassiveAggressivePerceptronLinearSoftMarginTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 07, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.math.matrix.mtj.SparseVectorFactoryMTJ;
import gov.sandia.cognition.math.matrix.VectorFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class OnlinePassiveAggressivePerceptron.LinearSoftMargin.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class OnlinePassiveAggressivePerceptronLinearSoftMarginTest
    extends OnlinePassiveAggressivePerceptronTest
{
    
    /**
     * Creates a new test.
     */
    public OnlinePassiveAggressivePerceptronLinearSoftMarginTest()
    {
        super();

        this.aggressiveCheck = false;
    }

    /**
     * Test of constructors of class OnlinePassiveAggressivePerceptron.
     */
    @Test
    public void testConstructors()
    {
        double aggressiveness = OnlinePassiveAggressivePerceptron.LinearSoftMargin.DEFAULT_AGGRESSIVENESS;
        VectorFactory<?> factory = VectorFactory.getDefault();
        OnlinePassiveAggressivePerceptron.LinearSoftMargin instance = new OnlinePassiveAggressivePerceptron.LinearSoftMargin();
        assertEquals(aggressiveness, instance.getAggressiveness(), 0.0);
        assertSame(VectorFactory.getDefault(), instance.getVectorFactory());

        aggressiveness = this.random.nextDouble();
        instance = new OnlinePassiveAggressivePerceptron.LinearSoftMargin(aggressiveness);
        assertEquals(aggressiveness, instance.getAggressiveness(), 0.0);
        assertSame(VectorFactory.getDefault(), instance.getVectorFactory());

        factory = new SparseVectorFactoryMTJ();
        instance = new OnlinePassiveAggressivePerceptron.LinearSoftMargin(aggressiveness, factory);
        assertEquals(aggressiveness, instance.getAggressiveness(), 0.0);
        assertSame(factory, instance.getVectorFactory());
    }


    @Override
    protected OnlinePassiveAggressivePerceptron createLinearInstance()
    {
        return new OnlinePassiveAggressivePerceptron.LinearSoftMargin();
    }

}