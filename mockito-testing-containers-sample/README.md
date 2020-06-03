# Mockito testing containers Sample

This sample shows how you can use the JCICSX API to get data from BIT containers. 
This sample contains a `CurrencyConverter` class which gets an account balance, and a conversion rate from 2 separate channels, and returns a string representing the value of the account once converted using the rate. 
In `src/test/java/mockitotests`, there is a test class `CurrencyConverterTest`, which demonstrates how to use a mocking framework such as Mockito to test the logic of your application by mocking out the JCICSX calls. By mocking out the JCICSX calls, CICS is not called, and these tests can be run in an agnostic environment. 
