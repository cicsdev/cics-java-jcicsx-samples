      *----------------------------------------------------------------*
      *  Licensed Materials - Property of IBM                          *
      *  SAMPLE                                                        *
      *  (c) Copyright IBM Corp. 2016 All Rights Reserved              *
      *  US Government Users Restricted Rights - Use, duplication or   *
      *  disclosure restricted by GSA ADP Schedule Contract with       *
      *  IBM Corp                                                      *
      *----------------------------------------------------------------*

      ******************************************************************
      *                                                                *
      * Module Name        CONVERT.CBL                                 *
      * Version            1.0                                         *
      * Date               03/06/2020                                  *
      *                                                                *
      * CICS back-end bit channel/container sample                     *
      *                                                                *
      * This program expects to be invoked with a BIT container named  *
      * INPUTDATA. INPUTDATA will contain a celcius temperature.       *
      *  and returns the following containers:                         *
      * A BIT container containing the temperatre input converted to   *
      *       fahrenheit                                               *
      * A BIT container containing the CICS return code from reading   *
      * the input container                                            *
      ******************************************************************

       IDENTIFICATION DIVISION.
       PROGRAM-ID. CONVERT.

       ENVIRONMENT DIVISION.
       CONFIGURATION SECTION.
       DATA DIVISION.
       WORKING-STORAGE SECTION.

      *  Container name declarations
      *  Channel and container names are case sensitive
       01 INPUT-CONT         PIC X(16) VALUE 'INPUTDATA'.
       01 OUTPUT-CONT        PIC X(16) VALUE 'OUTPUTDATA'.
       01 LENGTH-CONT        PIC X(16) VALUE 'INPUTDATALENGTH'.
       01 ERROR-CONT         PIC X(16) VALUE 'ERRORDATA'.
       01 RESP-CONT          PIC X(16) VALUE 'CICSRC'.


      *  Data fields used by the program
       01 INPUTLENGTH        PIC S9(8) COMP-4.
       01 DATALENGTH         PIC S9(8) COMP-4.
       01 ABENDCODE          PIC X(4) VALUE SPACES.
       01 CHANNELNAME        PIC X(16) VALUE SPACES.
       01 INPUTDATA          PIC S9(9) COMP-4.
       01 OUTPUTDATA         PIC S9(9) COMP-4.
       01 RESPCODE           PIC S9(8) COMP-4 VALUE 0.
       01 RESPCODE2          PIC S9(8) COMP-4 VALUE 0.
       01 RC-RECORD          PIC S9(8) COMP-4 VALUE 0.
       01 ERR-RECORD.
         03 ERRORCMD           PIC X(16) VALUE SPACES.
         03 ERRORSTRING        PIC X(32) VALUE SPACES.


       PROCEDURE DIVISION.
      *  -----------------------------------------------------------
       MAIN-PROCESSING SECTION.
      *  -----------------------------------------------------------

      *  Get name of channel
           EXEC CICS ASSIGN CHANNEL(CHANNELNAME)
                            END-EXEC.

      *  If no channel passed in, terminate with abend code NOCH
           IF CHANNELNAME = SPACES THEN
               MOVE 'NOCH' TO ABENDCODE
               PERFORM ABEND-ROUTINE
           END-IF.


      *  Read content and length of input container
           MOVE LENGTH OF INPUTDATA TO INPUTLENGTH.
           EXEC CICS GET CONTAINER(INPUT-CONT)
                            CHANNEL(CHANNELNAME)
                            FLENGTH(INPUTLENGTH)
                            INTO(INPUTDATA)
                            RESP(RESPCODE)
                            RESP2(RESPCODE2)
                            END-EXEC.

      *  Place RC in binary container for return to caller
           MOVE RESPCODE TO RC-RECORD.
           EXEC CICS PUT CONTAINER(RESP-CONT)
                            FROM(RC-RECORD)
                            FLENGTH(LENGTH OF RC-RECORD)
                            BIT
                            RESP(RESPCODE)
                            END-EXEC.

           IF RESPCODE NOT = DFHRESP(NORMAL)
             PERFORM RESP-ERROR
           END-IF.
           
      *  Convert celcius input to fahrenheit
            COMPUTE OUTPUTDATA ROUNDED = (INPUTDATA * 9/5) + 32.

      *  Place converted temperature in output container

           EXEC CICS PUT CONTAINER(OUTPUT-CONT)
                            CHANNEL(CHANNELNAME)
                            FROM(OUTPUTDATA)
                            FLENGTH(LENGTH OF OUTPUTDATA)
                            BIT
                            RESP(RESPCODE)
                            END-EXEC.

           IF RESPCODE NOT = DFHRESP(NORMAL)
             PERFORM RESP-ERROR
           END-IF.


      *  Return back to caller
           PERFORM END-PGM.

      *  -----------------------------------------------------------
       RESP-ERROR.
             MOVE 'EDUC' TO ABENDCODE
             PERFORM ABEND-ROUTINE.

           PERFORM END-PGM.

      *  -----------------------------------------------------------
      *  Abnormal end
      *  -----------------------------------------------------------
       ABEND-ROUTINE.
           EXEC CICS ABEND ABCODE(ABENDCODE) END-EXEC.

      *  -----------------------------------------------------------
      *  Finish
      *  -----------------------------------------------------------
       END-PGM.
           EXEC CICS RETURN END-EXEC.

