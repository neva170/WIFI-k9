package com.wxy.vpn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by PinesucceedAndroid on 7/3/2017.
 */

public class PrivacyPolicy extends AppCompatActivity {

    private TextView privacytext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.wxy.vpn.R.layout.activity_privacy);
        getSupportActionBar().setTitle("Privacy Policy");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        privacytext = (TextView) findViewById(com.wxy.vpn.R.id.privacytext);
        privacytext.setText("June 15, 2017\n" +
                "WiFi-K9 Inc.’s Privacy Policy\n" +
                "This Privacy Policy describes the information collected by WiFi-K9 Inc. (“WiFi-K9”, “us”, “we”), how that information may be used, with whom it may be shared, and your choices about such uses and disclosures. By visiting www.wifi-k9.com (the “Site”) or accessing or using the websites, mobile applications, products, and services provided by WiFi-K9 (together with the Site, the “Services”), you are accepting the practices described in this Privacy Policy. This Policy forms an integral part of the WiFi-K9 Terms of Use, which is hereby incorporated by reference. Any capitalized term used but not defined in this Privacy Policy will have the meaning defined in the Terms of Use.\n" +
                "\n" +
                "Please read this Privacy Policy carefully when using the Services.\n" +
                "The Information WiFi-K9 May Collect\n" +
                "1.\t  Information You Provide  \n" +
                "WiFi-K9 may collect and store any personal information you enter through the Services or the Site. This may include personally identifying information, such as your name, address, and e-mail address. WiFi-K9 maintains administrative, technical and physical safeguards intended to protect against the loss, misuse, unauthorized access, alteration, or disclosure of personal information. However, no system can be completely secure. Therefore, WiFi-K9 does not promise, and you should not expect, that such information may not be inadvertently collected and that such personal information will remain secure. \n" +
                "You also may give us permission to access your information on other services. If you connect to the Services using your Facebook, Google, WhatsApp, or other third-party service credentials (each, a “Supported Platform”), you authorize us to collect your authentication information, such as your username, encrypted access credentials, and other information that may be available on or through your Supported Platform account, including but not limited to your name and connections or contact lists. The information we get from Supported Platforms may depend on your settings or their privacy policies.\n" +
                "2.\tInformation Collected Automatically\n" +
                "In order to make sure our Services are available and to keep them running smoothly, we may collect the date and duration of time you connected to the Services, choice of server location, and the total amount of data sent and received while connected to the Services. WiFi-K9 is committed to your privacy, and therefore does not collect, store, or log traffic data, browsing activity, your IP address, or any other activities on your mobile device outside of the Services when connected to the Services.\n" +
                "\n" +
                "In using our Services, we may collect and process information about your actual location. You acknowledge and agree that the Services may automatically utilize the location features of your device in order to determine your location and provide you with nearby Hotspots and relevant advertisements. The Services require your location information for some features of the Services to work. If you have enabled GPS, geo-location or other location-based features on your device, you acknowledge that your device location will be tracked consistent with this Privacy Policy.\n" +
                "\n" +
                "Because there is not yet a consensus on how companies should respond to web browser-based or other do-not-track (\"DNT\") mechanisms, the Site does not respond to web browser-based DNT signals at this time.\n" +
                "3.\tCookies and Use of Cookie Data\n" +
                "Like many sites on the Internet, when you visit the Site, WiFi-K9 may assign your computer one or more cookies to facilitate access to the Site and to personalize your online experience. Through the use of a cookie, WiFi-K9 also may automatically collect information about your online activity on the Site, such as the web pages you visit, the time and date of your visits, and the links you click on the Site. WiFi-K9 may use this data to better understand how you interact with the Site, to monitor aggregate usage by our users and web traffic routing on the Site, and to improve the Site and our Services. Most browsers automatically accept cookies, but you can usually modify your browser settings to decline cookies. If you choose to decline cookies, please note that you may not be able use some of the interactive features offered on the Site. In addition, please be aware that other parties may also place their own cookies on or through the Site, and may collect or solicit personal information from you. Other cookies may reflect de-identified demographic or other data linked to the registration data you have submitted to WiFi-K9 in hashed, non-human readable form. No personally identifiable information is contained in these cookies.\n" +
                "4.\tInformation Collected by Third Parties\n" +
                "WiFi-K9 may allow third parties, advertising companies, and ad networks to display advertisements on the Site and/or the Services. These companies may use tracking technologies, such as cookies or pixel tags, to collect information about users who view or interact with their advertisements. WiFi-K9 does not share information that personally identifies you to these third parties.\n" +
                "You understand and agree that Supported Platforms’ or other third parties’ use of information they collect from you is governed by their privacy policies, and WiFi-K9’s use of such information is governed by this Privacy Policy.\n" +
                "5.\tAgents\n" +
                "WiFi-K9 employs or engages other companies and individuals to perform business functions on behalf of the Site and Services. These persons are provided with personal identifying information required to perform their functions, but are prohibited by contract from using the information for other purposes. These persons engage in a variety of functions which include, but are not limited to, removing repetitive information from user lists, analyzing data, providing marketing assistance, and providing user services.\n" +
                "As the technological landscape is constantly changing, no security system is absolutely secure. WiFi-K9 cannot and does not guarantee the security of any of its databases and information contained therein, nor can WiFi-K9 guarantee any supplied information will be absolutely secure from interception during transmission to the Site or Services over the Internet.\n" +
                "How WiFi-K9 Uses the Information It Collects\n" +
                "WiFi-K9 may use information that it collects as described above to:\n" +
                "•\tDeliver the services that you have requested.\n" +
                "•\tProvide you with customer support.\n" +
                "•\tEnforce or exercise any rights in WiFi-K9’s Terms of Use.\n" +
                "•\tPerform functions as otherwise described to you at the time of collection. \n" +
                "\n" +
                "WiFi-K9 may also release your personal information to a third-party in order to protect, establish, or exercise our legal rights or defend against legal claims; comply with a subpoena, court order, legal process, or other legal requirement; or when we believe in good faith that such disclosure is necessary to comply with the law, prevent imminent physical harm or financial loss, or investigate, prevent, or take action regarding illegal activities, suspected fraud, threats to our property, or as necessary or appropriate in connection with an investigation of fraud, intellectual property infringement, piracy, or other unlawful activity. \n" +
                "\n" +
                "Through your settings within the Services, you can choose which communications you receive from us. \n" +
                "Third-Party Websites\n" +
                "There are a number of places on the Site and the Services where you may click on a link to access other websites that do not operate under this Privacy Policy. For example, if you click on an advertisement on the Site or the Services, you may be taken to a website that WiFi-K9 does not control. These third party websites may independently solicit and collect information, including personal information, from you. WiFi-K9 recommends that you consult the privacy statements of all third party websites you visit by clicking on the \"privacy\" link typically located at the bottom of the webpage you are visiting.\n" +
                "Children's Privacy\n" +
                "WiFi-K9 forbids the use of the Site and Services to individuals under the age of 18. WiFi-K9 does not knowingly collect personal information from children under the age of 13.\n" +
                "Visiting the Site and Services From Outside the United States\n" +
                "This Privacy Policy is intended to cover collection of information on the Site and Services from residents of the United States. If you are visiting the Site or using the Services from outside the United States, please be aware that your information may be transferred to, stored, and processed in the United States where WiFi-K9's servers are located and WiFi-K9’s central database is operated. By using the Site or Services, you understand that your information may be transferred to WiFi-K9's facilities and those third parties with whom WiFi-K9 shares it as described in this Privacy Policy. You are responsible for ensuring that information you share conforms to all local data protection laws.\n" +
                "Changes to the Privacy Policy\n" +
                "WiFi-K9 will occasionally update this Privacy Policy. When WiFi-K9 posts changes to this Privacy Policy, it will revise the date at the top of this Privacy Policy. WiFi-K9 recommends that you check the Site and Services from time to time to inform yourself of any changes in this Privacy Policy or any of WiFi-K9's other policies.\n");

    }
}

