/*
 * File:                BayesianUtil.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 7, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.statistics.ClosedFormDistribution;
import gov.sandia.cognition.statistics.ComputableDistribution;
import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Contains generally useful utilities for Bayesian statistics.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class BayesianUtil
{

    /**
     * Computes the log likelihood of the i.i.d. data using the given
     * probability function.
     * @param <ObservationType>
     * Type of observations to consider
     * @param distribution
     * Computable distribution from which to get the probability function
     * (a pdf or pmf) used to compute the likelihood.
     * @param observations
     * Observations to compute the log likelihood of.
     * @return
     * Log likelihood of the i.i.d. data using the given probability function.
     */
    public static <ObservationType> double logLikelihood(
        final ComputableDistribution<? super ObservationType> distribution,
        final Iterable<? extends ObservationType> observations )
    {
        ProbabilityFunction<? super ObservationType> f =
            distribution.getProbabilityFunction();
        double logSum = 0.0;
        for( ObservationType observation : observations )
        {
            logSum += f.logEvaluate(observation);
        }
        return logSum;
    }

    /**
     * Samples from the given BayesianParameter by first sampling the prior
     * distribution, then updating the conditional distribution, then sampling
     * from the updated conditional distribution.
     * @param <ObservationType>
     * Type of observations generated by the conditional distribution
     * @param <ParameterType>
     * Type of parameters generated by the prior distribution, used to
     * update the conditional distribution
     * @param conditional
     * Conditional distribution that generates observations
     * @param parameterName
     * Name of the parameter in the conditional distribution that is
     * generated according to the prior distribution
     * @param prior
     * Prior distribution of parameter values
     * @param random
     * Random number generator
     * @param numSamples
     * Number of samples to generate
     * @return
     * Samples from the given BayesianParameter by first sampling the prior
     * distribution, then updating the conditional distribution, then sampling
     * from the updated conditional distribution.
     */
    public static <ObservationType,ParameterType> ArrayList<? extends ObservationType> sample(
        final ClosedFormDistribution<ObservationType> conditional,
        final String parameterName,
        final Distribution<ParameterType> prior,
        final Random random,
        final int numSamples )
    {
        DefaultBayesianParameter<ParameterType,ClosedFormDistribution<ObservationType>,Distribution<ParameterType>> parameter =
            new DefaultBayesianParameter<ParameterType, ClosedFormDistribution<ObservationType>, Distribution<ParameterType>>( conditional, parameterName, prior );
        return sample( parameter, random, numSamples );
    }

    /**
     * Samples from the given BayesianParameter by first sampling the prior
     * distribution, then updating the conditional distribution, then sampling
     * from the updated conditional distribution.
     * @param <ObservationType>
     * Type of observations generated by the conditional distribution
     * @param <ParameterType>
     * Type of parameters generated by the prior distribution, used to
     * update the conditional distribution
     * @param parameter
     * BayesianParameter that links the conditional distribution via a
     * parameter to a prior distribution.
     * @param random
     * Random number generator
     * @param numSamples
     * Number of samples to generate
     * @return
     * Samples from the given BayesianParameter by first sampling the prior
     * distribution, then updating the conditional distribution, then sampling
     * from the updated conditional distribution.
     */
    public static <ObservationType,ParameterType> ArrayList<ObservationType> sample(
        final BayesianParameter<ParameterType,? extends Distribution<ObservationType>,? extends Distribution<ParameterType>> parameter,
        final Random random,
        final int numSamples )
    {
        ArrayList<? extends ParameterType> parameters =
            parameter.getParameterPrior().sample(random, numSamples);
        ArrayList<ObservationType> samples =
            new ArrayList<ObservationType>( numSamples );
        for( int n = 0; n < numSamples; n++ )
        {
            parameter.setValue( parameters.get(n) );
            samples.add( parameter.getConditionalDistribution().sample(random) );
        }
        return samples;
    }


    /**
     * Computes the deviance of the model, which is
     * -2log(p(observations|parameter)).  This is proportional to the mean
     * squared error if the model is normal with constant variance.
     * @param <ObservationType>
     * @param conditional
     * Conditional distribution that generates observations.
     * @param observations
     * Observations to consider
     * @return
     * Deviance of the model.
     */
    @PublicationReference(
        author={
            "Andrew Gelman",
            "John B. Carlin",
            "Hal S. Stern",
            "Donald B. Rubin"
        },
        title="Bayesian Data Analysis, Second Edition",
        type=PublicationType.Book,
        year=2004,
        pages={180,181},
        notes="Equation 6.6"
    )
    public static <ObservationType> double deviance(
        final ComputableDistribution<ObservationType> conditional,
        final Iterable<? extends ObservationType> observations )
    {
        return -2.0 * logLikelihood(conditional, observations);
    }

    /**
     * Computes the expected deviance of the model by sampling parameters from
     * the posterior and then computing the deviance using the conditional
     * distribution. This is also proportional to the Kullback-Leibler
     * divergence of the posterior up to an additive constant.
     * In the limit of infinite data, the model with the lowest expected
     * deviance will have the highest posterior probability.
     * @param <ObservationType>
     * Type of observations generated by the conditional distribution.
     * @param <ParameterType>
     * Type of parameters generated by the posterior distribution.
     * @param predictiveDistribution
     * Relationship between the posterior (parameters) and the conditional
     * (observations).
     * @param observations
     * Observations to consider when computing the deviance
     * @param random
     * Random number generator
     * @param numSamples
     * Number of samples to use in the expectation.
     * @return
     * Expected deviance of the model.
     */
    @PublicationReference(
        author={
            "Andrew Gelman",
            "John B. Carlin",
            "Hal S. Stern",
            "Donald B. Rubin"
        },
        title="Bayesian Data Analysis, Second Edition",
        type=PublicationType.Book,
        year=2004,
        pages={180,181},
        notes="Equation 6.9"
    )
    public static <ObservationType,ParameterType> UnivariateGaussian expectedDeviance(
        final BayesianParameter<ParameterType,? extends ComputableDistribution<ObservationType>,?> predictiveDistribution,
        final Iterable<? extends ObservationType> observations,
        final Random random,
        final int numSamples )
    {

        ArrayList<? extends ParameterType> parameters =
            predictiveDistribution.getParameterPrior().sample(random, numSamples);
        ArrayList<Double> deviances = new ArrayList<Double>( parameters.size() );
        for( ParameterType parameter : parameters )
        {
            predictiveDistribution.setValue(parameter);
            deviances.add( deviance(
                predictiveDistribution.getConditionalDistribution(), observations ) );
        }

        return UnivariateGaussian.MaximumLikelihoodEstimator.learn(deviances,0.0);

    }

    /**
     * Computes the Monte Carlo distribution of the given samples.
     * @param samples
     * Samples to consider.
     * @return
     * Distribution describing the mean and estimated variance of the mean
     * of the samples.
     */
    public static UnivariateGaussian getMean(
        final Collection<? extends Double> samples)
    {
        Pair<Double,Double> pair = UnivariateStatisticsUtil.computeMeanAndVariance(samples);
        double mean = pair.getFirst();
        double variance = pair.getSecond() / samples.size();
        return new UnivariateGaussian( mean, variance );
    }

}
