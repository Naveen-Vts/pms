// language-toggle.js

const LANGUAGES = {

    en: {

        PMS_TITLE : "PROJECT MANAGEMENT SYSTEM (VER 1.7.0)",

        HOME : "Home",

        DPFM : "DPFM-2021",

        DPFM_HANDBOOK : "DPFM Handbook-2021",

        QUOTE : "Let's simplify project management",

        TAGLINE : "Analytics | Insights | Empowerment",

        WELCOME : "Welcome !",

        LOGIN : "Login",

        USERNAME : "Username",

        PASSWORD : "Password",

        LOGIN_WARNING : "* Do not share credentials with anyone",

        LICENSE_EXPIRED : "Your License has been Expired..!",

        WEBSITE_MAINTAINED : "Website maintained by Vedant Tech Solutions",

        SITE_VIEW :
        "Site best viewed at 1360 x 768 resolution in I.E / Microsoft Edge 110+, Mozilla 110+, Google Chrome 110+",

        VERSION_MISMATCH : "Version Mismatch",

        CONTINUE : "Still want to continue",
		
		LANGUAGE_TOOLTIP: "Change Language",

        BROWSER_MSG :
        "Your current Browser version is not supported.<br><br>Please ensure optimal viewing by using Internet Explorer (I.E) or Microsoft Edge 110+, Mozilla 110+, or Google Chrome 110+.<br><br>Site Best viewed at a resolution of 1360 x 768.<br><br>Thank You!"
    },



    hi: {

        PMS_TITLE : "प्रोजेक्ट मैनेजमेंट सिस्टम (संस्करण 1.7.0)",
		
		LANGUAGE_TOOLTIP: "भाषा बदलें",

        HOME : "होम",

        DPFM : "डीपीएफएम-2021",

        DPFM_HANDBOOK : "डीपीएफएम हैंडबुक-2021",

        QUOTE : "आइए परियोजना प्रबंधन को सरल बनाएं",

        TAGLINE : "विश्लेषण | अंतर्दृष्टि | सशक्तिकरण",

        WELCOME : "स्वागत है",

        LOGIN : "लॉगिन",

        USERNAME : "यूज़रनेम",

        PASSWORD : "पासवर्ड",

        LOGIN_WARNING : "* अपने लॉगिन विवरण किसी के साथ साझा न करें",

        LICENSE_EXPIRED : "आपका लाइसेंस समाप्त हो गया है!",

        WEBSITE_MAINTAINED : "वेबसाइट का रखरखाव वेदांत टेक सॉल्यूशंस द्वारा किया जाता है",

        SITE_VIEW :
        "यह वेबसाइट 1360×768 रिज़ॉल्यूशन एवं I.E./Edge 110+/Mozilla 110+/Chrome 110+ में सर्वोत्तम दिखाई देती है",

        VERSION_MISMATCH : "संस्करण मेल नहीं खा रहा",

        CONTINUE : "फिर भी जारी रखें",

        BROWSER_MSG :
        "आपका वर्तमान ब्राउज़र संस्करण समर्थित नहीं है।<br><br>कृपया Microsoft Edge 110+, Mozilla 110+ अथवा Google Chrome 110+ का उपयोग करें।<br><br>साइट 1360 × 768 रिज़ॉल्यूशन पर सर्वोत्तम दिखाई देती है।<br><br>धन्यवाद।"
    }

};


function changeLanguage(lang){

    localStorage.setItem("language", lang);

    let data = LANGUAGES[lang];

    $("[data-lang]").each(function () {
        let key = $(this).data("lang");
        if (data[key]) {
            $(this).html(data[key]);
        }
    });

    $("[data-placeholder]").each(function () {
        let key = $(this).data("placeholder");
        if (data[key]) {
            $(this).attr("placeholder", data[key]);
        }
    });

    // Update tooltip
    updateLanguageTooltip(lang);
}


$(document).ready(function(){

    let lang=localStorage.getItem("language") || "en";

    changeLanguage(lang);

});

function toggleLanguage() {

    let currentLang = localStorage.getItem("language") || "en";

    currentLang = currentLang === "en" ? "hi" : "en";

    changeLanguage(currentLang);
}




function updateLanguageTooltip(lang){

    const tooltip = document.getElementById("languageTooltip");

    if (!tooltip) return;

    tooltip.innerHTML = LANGUAGES[lang].LANGUAGE_TOOLTIP;
}