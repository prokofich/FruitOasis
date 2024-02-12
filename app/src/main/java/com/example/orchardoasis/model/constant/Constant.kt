package com.example.orchardoasis.model.constant

import com.example.orchardoasis.view.activity.GameActivity

lateinit var GAME:GameActivity

const val STATUS_INSTALLATION = "STATUS_INSTALLATION"
const val COUNT_START_APPLICATION = "COUNT_START_APPLICATION"
const val LAST_URL_IN_WEB = "LAST_URL_IN_WEB"
const val MAIN_URL_IN_WEB = "MAIN_URL_IN_WEB"

const val LEMON = "LEMON"
const val CHERRY = "CHERRY"
const val DIAMOND = "DIAMOND"
const val QUESTION = "QUESTION"

const val WIN = "WIN"
const val LOSS = "LOSS"

val listFruitsForGame = listOf(LEMON, LEMON, LEMON, LEMON, LEMON,
                                       CHERRY, CHERRY, CHERRY, CHERRY, CHERRY,
                                       DIAMOND, DIAMOND, DIAMOND, DIAMOND, DIAMOND, DIAMOND)

val listQuestionsForGame = listOf(QUESTION, QUESTION, QUESTION, QUESTION, QUESTION,
                                  QUESTION, QUESTION, QUESTION, QUESTION, QUESTION,
                                   QUESTION, QUESTION, QUESTION, QUESTION, QUESTION, QUESTION)

val listAllFruit = listOf(LEMON, CHERRY, DIAMOND)

const val FOR_GAME = "FOR_GAME"
const val FOR_WEBVIEW = "FOR_WEBVIEW"
const val FOR_WEBVIEW_REPEAT = "FOR_WEBVIEW_REPEAT"
const val TYPE = "TYPE"

const val COMPLEXITY = "COMPLEXITY"
const val EASY = 5
const val MIDDLE = 4
const val HARD = 3

const val DEEPLINK = "deeplink"
const val ORGANIC = "organic"
const val NON_ORGANIC = "non_organic"

const val START_TIME = "START_TIME"

const val MAIN_ATTRIBUTE = "MAIN_ATTRIBUTE"

const val ADVERTISING_ID = "ADVERTISING_ID"

const val ORGANIC_INSTALL = "ORGANIC_INSTALL"
const val NOT_ORGANIC_INSTALL = "NOT_ORGANIC_INSTALL"

const val USER_ID = "USER_ID"

const val ONESIGNAL_APP_ID = "af179aca-49a2-47ff-adb3-250a300b462c"
const val AMPLITUDE_API_KEY = "5a870b9986055dc912b79cfb8b509662"
const val FACEBOOK_ID = "767031205256128"