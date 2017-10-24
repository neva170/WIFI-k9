package com.wxy.vpn;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wxy.vpn.api.ApiK9Server;
import com.wxy.vpn.fragments.GuiHelperUtils;
import com.wxy.vpn.utils.Connectivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignupByEmail extends AppCompatActivity
        implements View.OnClickListener, Callback<ApiK9Server.ApiResponse> {

    public static final String EMAIL_FOR_LOGIN = "EMAIL_FOR_LOGIN_FROM_SIGNUP";

    private final String TAG = this.getClass().getSimpleName();

    private TextInputLayout mFullNameView;
    private TextInputLayout mEmailView;
    private TextInputLayout mPasswordView;
    private TextInputLayout mPasswordRepeatView;
    private CheckBox mtos,mTerms, mprivacy;
    private Spinner mCitizenship;

    private ProgressDialog mProgressDialog;

    private ApiK9Server.ApiInterface api;
    private GuiHelperUtils.EmailValidator mEmailValidator;
    private GuiHelperUtils.PasswordValidator mPasswordValidator, mPasswordRepeatValidator;
    private GuiHelperUtils.RequiredFieldValidator mRequiredFieldValidator;
    private TextView terms;
    private TextView tos;
    private TextView privacy;
    private String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.wxy.vpn.R.layout.activity_signup_by_email);

        setupCitizenshipInput();

        Button btnRegister = (Button) findViewById(com.wxy.vpn.R.id.btn_register);
        btnRegister.setOnClickListener(this);

        setupFullnameInput();
        setupEmailInput();
        setupPasswordInput();
        setupPasswordRepeatInput();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if( null != intent){
            phone = intent.getStringExtra("phone");
        }

        mTerms = (CheckBox) findViewById(com.wxy.vpn.R.id.terms_of_services);
     //   mTerms = (CheckBox) findViewById(R.id.signup_accept_terms);
        mprivacy = (CheckBox) findViewById(com.wxy.vpn.R.id.signup_accept_privacy);
    //    terms = (TextView) findViewById(R.id.terms);
        tos = (TextView) findViewById(com.wxy.vpn.R.id.tos);
        privacy = (TextView) findViewById(com.wxy.vpn.R.id.privacy);

        tos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(SignupByEmail.this).setTitle("Terms of Services").setMessage("WiFi-K9 Inc. Terms of Service\n" +
                        "These Terms of Service are effective as of June 15, 2017\n" +
                        "Welcome to WiFi-K9. The WiFi-K9 websites, mobile applications, products, and services are provided by WiFi-K9 Inc., a Delaware Public Benefit Corporation (“we”, “us”, “WiFi-K9”). Your use of the WiFi-K9 websites, mobile applications, products, and services (hereinafter collectively referred to the “Services”) is subject to these terms of use (these “Terms”).\n" +
                        "Lawyers tell us to say:\t\t\tWhich means:\n" +
                        "\t\t\t\n" +
                        "\tThese Terms govern your use of the Services and by using the Services, you agree to be bound by the Terms as well as our Privacy Policy, which is hereby incorporated into these Terms by reference.\t\t\tThis contract and our Privacy Statement cover your use of our service.\n" +
                        "1. Acceptance\t\t\t\n" +
                        "\tPLEASE READ THESE TERMS CAREFULLY. BY CREATING A WIFI-K9 ACCOUNT, OR DOWNLOADING, INSTALLING OR OTHERWISE ACCESSING OR USING THE SERVICES OR ANY PORTION THEREOF, YOU HEREBY ACKNOWLEDGE AND AGREE THAT YOU HAVE READ, UNDERSTOOD, AND AGREE TO BE BOUND BY THESE TERMS, INCLUDING THE PRIVACY POLICY.\t\t\tBy using WiFi-K9, you agree to this contract and our Privacy Policy.\n" +
                        "2. Changes to the Terms of Service\t\t\t\n" +
                        "\tWe may modify these Terms from time to time. The most current version of the Terms will be located on www.wifi-k9.com (the “Site”). You understand and agree that your access to or use of the Services is governed by these Terms effective at the time of your access to or use of the Services. If we make material changes to these Terms, we will notify you by email or by posting a notice on the Services prior to the effective date of the changes. We will also indicate at the top of this page the date that revisions were last made. You should revisit these Terms on a regular basis, as revised versions will be binding on you. Any such modification will be effective upon our posting of new Terms. You understand and agree that your continued access to or use of the Service after the effective date of modifications to the Terms indicates your acceptance of the modifications.\t\t\tWe might update the Terms of Service as we add new features or for other reasons. If you use WiFi-K9 after any update, you agree to those updated Terms of Service.\n" +
                        "3. Our License to You\t\t\t\n" +
                        "\tSubject to these Terms and our policies, we grant you a limited, non-exclusive, non-transferable, and revocable license to use the Services.\t\t\tYou can use WiFi-K9, but you have to follow our rules\n" +
                        "4. Translation\t\t\t\n" +
                        "\tWe may translate these Terms into other languages for your convenience. Nevertheless, the English version governs your relationship with WiFi-K9. To the extent any translated version of these Terms conflicts with the English version, the English version controls.\t\t\tWe speak English, so if these Terms are in another language we can’t be responsible for any translation errors.\n" +
                        "\n" +
                        "5. Use of Services\t\t\t\n" +
                        "\tEligibility\t\t\t\n" +
                        "\tYou must be 18 years of age or older to use the Services. By using the Services, you represent that you are 18 or older, and that you will not permit a minor under the age of 18 to use the Services, your account, or otherwise interact with the Services. WiFi-K9 will never knowingly solicit or accept personally identifiable information or other content from a user or visitor who WiFi-K9 knows is under 18 years of age. If WiFi-K9 discovers that a user under 18 years of age has created an account, WiFi-K9 will terminate the account and remove the information or other content.\t\t\tSorry, you must be at least 18 years old to use WiFi-K9. We don’t make the laws, we just follow them.\n" +
                        "\tAvailability of Services or any Portion of the Services\t\t\t\n" +
                        "\tThe Services or any portion of the Services, including any and all mobile apps and applications, may be modified, updated, interrupte¬¬d, suspended or discontinued at any time without notice or liability. You understand and agree that your use of Services is at your own risk.\n" +
                        "We will use reasonable efforts to make the mobile apps or applications that are part of the Services available at all times. However, you acknowledge that the speed and quality of the Services may vary, and the Services are subject to unavailability, including third party service failures; emergencies; transmission, equipment or network problems or limitations; signal strength; interference; and maintenance and repair, and may be refused, interrupted, limited or diminished. WiFi-K9 is not responsible for any failures to maintain the accuracy, quality, confidentiality, or security of your data, messages or pages whether or not related to interruptions or performance issues with the Services. \n" +
                        "You acknowledge that you may log into the Services using certain third party sites and services, including but not limited to Facebook and Google (the “Supported Platform(s)”), and that your ability to do so is highly dependent on the availability of such Supported Platforms. If at any time any Supported Platforms cease to make their programs available to WiFi-K9 on reasonable terms, WiFi-K9 may cease to provide such features to you without entitling you to refund, credit, or other compensation. In order to use the features of the Services related to the Supported Platforms, you may be required to register for or log into such Supported Platforms on their respective websites. By enabling such Supported Platforms within the Service, you are allowing WiFi-K9 to pass your log-in information to these Supported Platforms for this purpose.\t\t\tWe do our best, but sometimes things beyond our control happen.\n" +
                        "\tSystem Requirements for Applications\t\t\t\n" +
                        "\tIn order to use any mobile apps or applications that are part of the Services, you are required to have a compatible device, Internet access, and the necessary minimum specifications (the “System Requirements”). You hereby acknowledge that the System Requirements may change from time to time, without notice, and that WiFi-K9 makes no representations as to the accuracy of the System Requirements.\n" +
                        "Some of the Services may be software that is downloaded to your computer, phone, tablet, or other device. You agree that we may automatically upgrade those Services, and these Terms will apply to such upgrades. You may further be required to obtain software and/or hardware updates or upgrades from time to time, as may be necessary in order to continue to use of the Services. You hereby acknowledge and agree that obtaining and maintaining the applicable System Requirements remains your responsibility.\t\t\tYou might need to upgrade your mobile device or download updates in order to use WiFi-K9.\n" +
                        "6. Payment\t\t\t\n" +
                        "\tYou acknowledge that you are bound by the terms of agreement with your mobile network or Internet provider (collectively, the “Mobile Provider”), which will continue to apply when using the Services. As a result, you may be charged by the Mobile Provider for any such third-party charges as may arise. You hereby acknowledge that WiFi-K9 is not responsible for such charges or services. If you are not the authorized party to make payment for any Mobile Provider charges incurred to access the Services, you hereby warrant and represent that you have all necessary authority from such authorized party to use the Services.\t\t\tYou still have to pay any applicable mobile provider charges when you use WiFi-K9.\n" +
                        "7. User Accounts and User Information\t\t\t\n" +
                        "\tYou must create an account and provide certain information about yourself in order to use the Services. You are responsible for maintaining the confidentiality of your account password. You are also responsible for all activities that occur in connection with your account. You agree to notify us immediately of any unauthorized use of your account. We reserve the right to terminate or suspend your account at any time for any or no reason.\t\t\tDon’t share your account information with anyone else.\n" +
                        "\tIn creating your account, we ask that you provide complete, accurate, and up-to-date information. You may not create or use an account for anyone other than yourself, provide a name, address, or email address other than your own, or create multiple accounts.\t\t\tPlease tell us the truth about yourself.\n" +
                        "\n" +
                        "\tYou agree to use these Services only for purposes as laid out by the (a) Terms and (b) any applicable law. You agree that you are solely responsible for any breach of your obligations under these Terms and the consequences of any breach arising. You agree to the use of your data in accordance with WiFi-K9 Privacy Policy. You shall not engage in activities which interfere with or disrupt the Services or our servers or in any way in connection to the Services.\t\t\tYou’re responsible for what you do when you use WiFi-K9.\n" +
                        "\n" +
                        "8. Communications from WiFi-K9\t\t\t\n" +
                        "\tBy creating an account, you agree to receive certain communications in connection with the Services. For example, we may periodically send you emails, including but not limited to information related to your WiFi-K9 account.\t\t\tWe might email you important information.\n" +
                        "9. Advertising\t\t\t\n" +
                        "\tWiFi-K9 and its licensees may publicly display advertisements and other information within the Services. Such advertisements may be targeted to the information on the Services, your location, or any other information. You are not entitled to any compensation for such advertisements. The manner, mode and extent of such advertising are subject to change without specific notice to you.  The Services may contain links to other websites maintained by third parties. These links are provided solely as a convenience and do not imply endorsement of, or association with, the third parties by WiFi-K9.\t\t\tWiFi-K9 shows advertisements so that we can keep the service free.\n" +
                        "10. Hotspot Submissions and Other User Content\t\t\t\n" +
                        "\tThe Services may permit you and other users to submit locations where a public Internet connection may be available (each, a “Hotspot”). You are responsible for the Hotspot locations and any other information, images, photos, drawings, icons, text and/or other content (“Content”) that you provide or otherwise make available to the Services, including without limitation its legality, accuracy, reliability, and appropriateness.\t\t\tYou are responsible for any content you submit to us.\n" +
                        "\tYou authorize WiFi-K9 to publish the Hotspot information you submit so that it may be accessed by other users of the Services, and you waive any rights of attribution and/or credit for such Hotspot information. Following termination or deactivation of your account, or upon removal any Hotspot information from WiFi-K9, we may retain the Hotspot information you provide for backup, archival, or audit purposes. \t\t\tWe can use any content you submit to us.\n" +
                        "\tWe Do Not Review or Verify User-Submitted Hotspots On the Services\t\t\t\n" +
                        "\tThe Hotspot locations and any other content available through the Services has not been reviewed, verified or authenticated by WiFi-K9, and may include inaccuracies or false information or be offensive, indecent or objectionable. WiFi-K9 cannot make any representations, warranties, or guarantees in connection with the Services, including relating to the accuracy, quality, usability, or suitability of any Hotspot. Any Hotspot that is provided by a business, organization, or other third party is not endorsed by or associated with WiFi-K9 and vice versa. You hereby acknowledge and affirm that it is your sole responsibility for and assume all risk arising from your use or reliance of connecting to any Hotspot, whether while using the Services or not. If notified by a user or Hotspot owner that a Hotspot is allegedly inaccurate does not conform to the Terms, we may investigate the allegation and determine in our sole discretion whether to remove the Hotspot, which we reserve the right to do at any time and without notice.\t\t\tWe don’t verify any content submitted to us by other users.\n" +
                        "\tFeedback\t\t\t\n" +
                        "\tWe value hearing from our users, and are always interested in learning about ways we can improve the WiFi-K9 Services. If you choose to submit comments, ideas or feedback, you agree that we are free to use them without any restriction or compensation to you. By accepting your submission, WiFi-K9 does not waive any rights to use similar or related feedback previously known to WiFi-K9, or developed by its employees, or obtained from sources other than you.\t\t\tIf you give us ideas on how to improve WiFi-K9, we can use them.\n" +
                        "\n" +
                        "11. Security\t\t\t\n" +
                        "\tWe care about the data security and privacy of our users. While we work to maintain the security of your content and account, WiFi-K9 cannot guarantee that unauthorized third parties will not be able to defeat our security measures. Please notify us immediately of any compromise or unauthorized use of your account.\n" +
                        "\t\t\tWe do our best to keep WiFi-K9 secure, but that doesn’t mean someone won’t outsmart us.\n" +
                        "12. Third Parties\t\t\t\n" +
                        "\tThird Party Links, Sites, and Services\t\t\t\n" +
                        "\tThe Services may include content, information, and/or data from, or links to, other apps, web apps, websites, or services, including but not limited to the Supported Platforms (\"Third Party Content\"). We do not control, assume responsibility for or endorse any Third Party Content. You agree that we are not responsible for the availability or contents of such Third Party Content. Your use of Third Party Content is at your own risk and you agree that WiFi-K9 will have no liability arising from your use of or access to any Third Party Content. When you access the Third Party Content, you will do so at your own risk. Any use of Third Party Content is governed solely by the terms and conditions of such Third Party Content provider (and you shall comply with all such terms and conditions), and any contract entered into, or any transaction completed via any Third Party Content provider or Supported Platform, is between you and the relevant third party, and not WiFi-K9. WiFi-K9 makes no representation and shall have no liability or obligation whatsoever in relation to the content or use of, or correspondence with, any such Third Party Content or any transactions completed and any contract entered into by you with any such third party.\t\t\tWe can’t control what others do.\n" +
                        "\tThird Party Terms of Use\t\t\t\n" +
                        "\tThe Services may, from time to time, access Third Party Content by accessing third party Application Programing Interfaces (“APIs”) within the Services. You understand and agree that the Services are not endorsed, certified or otherwise approved in any way by the third party providing such API and the provider of the API is not responsible for the Services. Notwithstanding any license provided under these Terms (including the end user license granted under these Terms), (i) any such third party API is provided “as-is,” without any warranties and all implied warranties, including the implied warranties of merchantability, fitness for a  particular purpose and non-infringement, are expressly disclaimed; (ii) you may not modify or create derivative works based on any part of any such third party API; (iii) you may not decompile, reverse-engineer, disassemble, and/or otherwise reduce any such third party API to source code or other human-perceivable form, to the full extent allowed by law; (iv) ownership of any such third party API and any services related to any such third party API remain with the owner of the API; and (v) the provider of any third-party API used in connection with the Services disclaims any and all liability on the part of the third-party API provider for any interruption in its services as accessed via the Services.\t\t\tWe can’t control any other services that are incorporated into WiFi-K-9.\n" +
                        "13. Proprietary Rights\t\t\t\n" +
                        "\tYou acknowledge and agree that WiFi-K9 owns all legal right, title and interest in and to the Services, including any intellectual property rights which subsist in the Services (whether those rights happen to be registered or not, and wherever in the world those rights may exist), and that the Services may contain information which may be of confidential nature to WiFi-K9 and you have no right to disclose such information.\n" +
                        "WiFi-K9, the WiFi-K9 logo, and other WiFi-K9 trademarks, service marks, graphics and logos used in connection with the Services are trademarks or registered trademarks of WiFi-K9 Inc. (collectively “WiFi-K9 Marks”). Other trademarks, service marks, graphics and logos used in connection with the Services are the trademarks of their respective owners (collectively “Third Party Marks”). The WiFi-K9 Marks and Third Party Marks may not be copied, imitated or used, in whole or in part, without the prior written permission of WiFi-K9 or the applicable trademark holder. The Services and all Content contained therein (including Third Party Content) are protected by copyright, trademark, patent, trade secret, international treaties, laws and other proprietary rights, and may have security components that protect digital information only as authorized by WiFi-K9 or the owner of the Content.\n" +
                        "You may not use any of our domain names without prior written consent by WiFi-K9.  You will not use any trademark, service mark, trade name, and logo of any company or organization in a method that is likely to cause or intends to cause confusion about ownership and authority.  \t\t\tRespect our rights in our software and brand name.\n" +
                        "14. Restrictions and Guidelines\t\t\t\n" +
                        "\tWe are under no obligation to enforce the Terms on your behalf against another user. While we encourage you to let us know if you believe another user has violated the Terms, we reserve the right to investigate and take appropriate action at our sole discretion. \t\t\tDon’t use WiFi-K9 to break the law or violate anyone else’s rights.\n" +
                        "\tYou agree not to, and will not assist, encourage, or enable others to use the Services to, and will not on behalf of anyone else:\n" +
                        "•\tViolate any third party's rights, including any breach of confidence, copyright, trademark, patent, trade secret, moral right, privacy right, right of publicity, or any other intellectual property or proprietary right;\n" +
                        "•\tThreaten, stalk, harm, or harass others, or promote bigotry or discrimination; \n" +
                        "•\tSend bulk emails, surveys, or other mass messaging, whether commercial in nature or not, engage in keyword spamming, or otherwise attempt to manipulate the Services’ search results or any third party web applications or services;\n" +
                        "•\tSolicit personal information from minors; \n" +
                        "•\tMask any person’s identity for illegal or malicious purposes; or\n" +
                        "•\tViolate any applicable law.\n" +
                        "You also agree you will not, and will not assist, encourage, or enable any users or third parties to, and will not on behalf of anyone else:\n" +
                        "•\tViolate these Terms;\n" +
                        "•\tModify, adapt, appropriate, reproduce, distribute, translate, create derivative works or adaptations of, publicly display, sell, trade, or in any way exploit the Services, mobile app or application, or any Content within the Services (other than Content you provide), except as expressly authorized by WiFi-K9;\n" +
                        "•\tUse any robot, spider, search/retrieval application, or other automated device, process or means to access, retrieve, scrape, or index any portion of the Services;\n" +
                        "•\tReverse engineer any portion of the Services;\n" +
                        "•\tRemove or modify any copyright, trademark or other proprietary rights notice that appears on any portion of the Services or on any materials printed or copied from the Services;\n" +
                        "•\tResell, reproduce, duplicate, copy or trade the Services for any purpose;\n" +
                        "•\tAccess, retrieve or index any portion of the Services for purposes of constructing or populating a database;\n" +
                        "•\tReformat or frame any portion of the Services;\n" +
                        "•\tTake any action that imposes, or may impose, in our sole discretion, an unreasonable or disproportionately large load on Services' technology infrastructure or otherwise make excessive traffic demands of the Services;\n" +
                        "•\tAttempt to gain unauthorized access to the Services, user accounts, computer systems or networks connected to the Services through hacking, password mining or any other means;\n" +
                        "•\tUse the Services to transmit any computer viruses, worms, defects, Trojan horses or other items of a destructive nature (collectively, \"Viruses\");\n" +
                        "•\tUse any device, software or routine that interferes with the proper working of the Services, or otherwise attempt to interfere with the proper working of the Services;\n" +
                        "•\tUse the Services to violate the security of any computer network, crack passwords or security encryption codes; disrupt or interfere with the security of, or otherwise cause harm to, the Services or any portion thereof; \n" +
                        "•\tRemove, circumvent, disable, damage or otherwise interfere with any security-related features of the Services, features that prevent or restrict the use or copying of content, or features that enforce limitations on the use of the Services;\n" +
                        "•\tBreak or circumvent the Services’ security measures or otherwise test the vulnerability of our systems or networks;\n" +
                        "•\tAccess the Services by any other means other than the applications, mobile apps, and/or other software provided by WiFi-K9;\n" +
                        "•\tDo anything that violates applicable law or regulations;\n" +
                        "•\tShare your password, let anyone access your account, or do anything that might put your account at risk; or\n" +
                        "•\tSell your username or account or otherwise transfer it for compensation.\n" +
                        "You will at all times abide by all applicable local, state, national and foreign laws, treaties and regulations in connection with your use of the Services, including but not limited to those related to data privacy.\t\t\tDon’t do any of the things listed to the left.\n" +
                        "\tTerritorial Restrictions\t\t\t\n" +
                        "\tThe information and or features provided within the Services is not intended for distribution to or use by any person or entity in any jurisdiction or country where such distribution or use would be contrary to law or regulation or which would subject WiFi-K9 to any registration requirement within such jurisdiction or country. We reserve the right to limit the availability of our Services or any portion of the Services, to any person, geographic area, or jurisdiction, at any time and in our sole discretion, and to limit the quantities or features of any content, product, service or other feature that we provide. \n" +
                        "Services provided by WiFi-K9 may be subject to United States export controls. Thus, no software from the Service may be downloaded, exported or re-exported: (a) into (or to a national or resident of) any country to which the United States has embargoed goods; or (b) to anyone on the U.S. Treasury Department's list of Specially Designated Nationals or the U.S. Commerce Department's Table of Deny Orders. By downloading any software or applications related to the Services, you represent and warrant that you are not located in, under the control of, or a national or resident of, any such country or on any such list. \t\t\tDon’t use or download WiFi-K9 in a place where it would break the law.\n" +
                        "\n" +
                        "15. Termination\t\t\t\n" +
                        "\tWe reserve the right in our sole discretion to terminate, disable, or suspend your account, your access to or use of the Services or any portion thereof, inclusive of any mobile or other apps or applications software or to terminate your account and these Terms at any time, with or without notice.  If you breach any of the provisions of these Terms, your right to use the Services will automatically be terminated. You may terminate these Terms at any time, with or without notice, by deleting your account and discontinuing all access to and use of the Services. In the event that your account is terminated by you or us, you will promptly remove all copies of the Services or any portion thereof, inclusive of any mobile or other apps or applications software, from your possession and control. Upon termination of these Terms, any provision that by its nature or express terms should survive will survive such termination or expiration, including, but not limited to the terms contained in the sections labeled “Payment,” the license you grant in “Restrictions and Guidelines,” “Indemnification,” “Third Parties,” “Proprietary Rights,” “Disclaimer of Warranty and Limitation of Liability,” “General Terms,” and “Dispute Resolution and Arbitration”, and our Privacy Policy (for information provided during the duration of the Terms).\t\t\tWe can revoke your right to use WiFi-K9.\n" +
                        "16. Indemnification\t\t\t\n" +
                        "\tYou agree to indemnify, defend, and hold WiFi-K9, its parents, subsidiaries, affiliates, any related companies, suppliers, licensors and partners, and the officers, directors, employees, agents and representatives of each of them harmless, including costs, liabilities, claims, suits, proceedings, disputes, demands, liabilities, damages, losses, costs and expenses, and legal fees, from any claim or demand made by any third party arising out of or relating to (i) your access to or use of the Services or any portion thereof, (ii) your violation of these Terms, (iii) any products or services purchased or obtained by you in connection with the Services or any portion thereof, (iv) the infringement by you, or any third party using your account, of any intellectual property, privacy, or other right of any person or entity, or (v) any illegal, threatening, harmful, or harassing acts committed by you, or any third party using your account. WiFi-K9 reserves the right, at your expense, to assume the exclusive defense and control of any matter for which you are required to indemnify us and you agree to cooperate with our defense of these claims. You agree not to settle any such matter without the prior written consent of WiFi-K9. WiFi-K9 will use reasonable efforts to notify you of any such claim, action or proceeding upon becoming aware of it.\t\t\tIf we are sued because of your use of WiFi-K9, you’re responsible for our expenses.\n" +
                        "17. Disclaimer of Warranty and Limitation of Liability\t\t\t\n" +
                        "\tWiFi-K9 is not responsible for any Third Party Content, or any other Content on the Services, whether posted or caused by users of the Services, WiFi-K9, third parties or by any of the equipment or programming associated with or utilized in the Services, including but not limited to Hotspot listings. WiFi-K9 assumes no responsibility for any error, omission, interruption, deletion, defect, delay in operation or transmission, communications line failure, theft or destruction or unauthorized access to, or alteration of, user communications while using the Services.\t\t\tWe’re not responsible for anyone else’s content or services.\n" +
                        "\tYou understand that it is your duty to confirm and verify any information provided on or through the Site and Services, and that you bear the sole risk of relying on any such information, including but not limited to Content, Third-Party Content, Hotspot locations, or links. WiFi-K9 is not responsible for any problems or technical malfunction of any telephone network or lines, cellular data provider or network, computer online systems, servers or providers, computer equipment, software, Hotspot, or Supported Platforms on account of technical problems or traffic congestion on the Services or Site, including injury or damage to users or to any other person's computer, and/or mobile device.\t\t\tWe’re not responsible for content or services not provided by us.\n" +
                        "\tNeither WiFi-K9 nor any of its affiliates, advertisers, promoters or distribution partners shall be responsible for any loss or damage, including personal injury or death, resulting from anyone's use of the Services, any Content, use of any Hotspot, or any failure to maintain the confidentiality, security, accuracy or quality of your data, messages or pages.\t\t\tWe’re not responsible for anyone else’s behavior.\n" +
                        "\tWE TRY TO KEEP SERVICES UP, BUG-FREE, AND SAFE, BUT YOU USE THEM AT YOUR OWN RISK. THE INFORMATION FROM OR THROUGH THE SITE AND THE SERVICES IS PROVIDED \"AS IS,\" \"AS AVAILABLE,\" AND ALL WARRANTIES, EXPRESS OR IMPLIED, ARE DISCLAIMED. THE CONTENT AND THE SERVICES MAY CONTAIN VIRUSES, BUGS, ERRORS, PROBLEMS OR OTHER LIMITATIONS. IN NO EVENT WILL WIFI-K9 OR ITS DIRECTORS, OFFICERS, EMPLOYEES OR AGENTS HAVE ANY LIABILITY WHATSOEVER FOR YOUR USE OF ANY CONTENT OR SERVICES. WE ARE NOT LIABLE FOR ANY INDIRECT, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES (INCLUDING DAMAGES FOR LOSS OF BUSINESS, LOSS OF PROFITS, LITIGATION, OR THE LIKE), WHETHER BASED ON BREACH OF CONTRACT, BREACH OF WARRANTY, TORT (INCLUDING NEGLIGENCE), PRODUCT LIABILITY OR OTHERWISE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. NO ADVICE OR INFORMATION OBTAINED BY YOU FROM US THROUGH THE SERVICES SHALL CREATE ANY WARRANTY, REPRESENTATION OR GUARANTEE NOT EXPRESSLY STATED IN THIS AGREEMENT.\n" +
                        "WIFI-K9 DOES NOT CONTROL THE INFORMATION OR CONTENT PROVIDED BY THIRD PARTIES ON THE SERVICES, AND THEREFORE SHALL NOT BE RESPONSIBLE FOR YOUR RELIANCE ON ANY INFORMATION OR STATEMENTS MADE ON OR THROUGH THE SERVICES, INCLUDING BUT NOT LIMITED TO THROUGH ADVERTISEMENTS OR HOTSPOT LISTINGS. WIFI-K9 IS NOT RESPONSIBLE OR LIABLE IN ANY MANNER FOR ANY THIRD PARTY SERVICES, HOTSPOT, OR SUPPORTED PLATFORMS ASSOCIATED WITH OR UTILIZED IN CONNECTION WITH THE SERVICES, INCLUDING THE FAILURE OF ANY SUCH THIRD PARTY SERVICES, HOTSPOT OR SUPPORTED PLATFORMS.\t\t\tWe’re not responsible if something goes REALLY wrong. We do our best, but we can’t guarantee anything.\n" +
                        "\tWIFI-K9’S LIABILITY TO YOU FOR ANY CAUSE WHATSOEVER, AND REGARDLESS OF THE FORM OF THE ACTION, WILL AT ALL TIMES BE LIMITED TO THE FEES, IF ANY, PAID BY YOU TO US FOR THE SERVICES WITHIN THE PRIOR THREE (3) MONTHS, BUT IN NO CASE WILL OUR LIABILITY TO YOU EXCEED $50. YOU AGREE THAT DISPUTES BETWEEN YOU AND WIFI-K9 WILL BE RESOLVED BY BINDING, INDIVIDUAL ARBITRATION AND YOU WAIVE YOUR RIGHT TO PARTICIPATE IN A CLASS ACTION LAWSUIT OR CLASS-WIDE ARBITRATION. YOU ACKNOWLEDGE THAT IF NO FEES ARE PAID TO US FOR THE SITE AND THE SERVICES, YOU SHALL BE LIMITED TO INJUNCTIVE RELIEF ONLY, UNLESS OTHERWISE PERMITTED BY LAW, AND SHALL NOT BE ENTITLED TO DAMAGES OF ANY KIND FROM US, REGARDLESS OF THE CAUSE OF ACTION. IF YOU ARE A CALIFORNIA RESIDENT, YOU WAIVE CALIFORNIA CIVIL CODE SECTION 1542, WHICH STATES, IN PART: \"A GENERAL RELEASE DOES NOT EXTEND TO CLAIMS WHICH THE CREDITOR DOES NOT KNOW OR SUSPECT TO EXIST IN HIS FAVOR AT THE TIME OF EXECUTING THE RELEASE, WHICH IF KNOWN BY HIM MUST HAVE MATERIALLY AFFECTED HIS SETTLEMENT WITH THE DEBTOR\".\t\t\tOur liability to you is limited.\n" +
                        "18. Time Limitation on Claims\t\t\t\n" +
                        "\tYou agree that any claim you may have arising out of or related to your relationship with WiFi-K9 must be filed within one (1) year after such claim arose; otherwise, your claim is permanently barred.\t\t\tIf you have a dispute with us, you have one year to file a claim.\n" +
                        "19. Dispute Resolution and Arbitration\t\t\t\n" +
                        "\tFor any dispute you have with WiFi-K9, you agree to first contact us and attempt to resolve the dispute with us informally. You agree that, if WiFi-K9 has not been able to resolve the dispute with you informally, to resolve any claim, dispute, or controversy (excluding claims for injunctive or other equitable relief) arising out of or in connection with or relating to these Terms by binding arbitration by except for claims for injunctive or equitable relief or claims regarding intellectual property rights (which may be brought in any competent court without the posting of a bond), shall be finally settled in accordance with the Comprehensive Arbitration Rules of the Judicial Arbitration and Mediation Service, Inc. (“JAMS”) by arbitrators appointed in accordance with such rules. The award rendered by the arbitrator shall include costs of arbitration, reasonable attorneys' fees and reasonable costs for expert and other witnesses, and any judgment on the award rendered by the arbitrator may be entered in any court of competent jurisdiction. Nothing in this Section shall prevent either party from seeking injunctive or other equitable relief from the courts for matters related to data security, intellectual property or unauthorized access to the Service. ALL CLAIMS MUST BE BROUGHT IN THE PARTIES' INDIVIDUAL CAPACITY, AND NOT AS A PLAINTIFF OR CLASS MEMBER IN ANY PURPORTED CLASS OR REPRESENTATIVE PROCEEDING, AND, UNLESS WE AGREE OTHERWISE, THE ARBITRATOR MAY NOT CONSOLIDATE MORE THAN ONE PERSON'S CLAIMS. YOU AGREE THAT, BY ENTERING INTO THESE TERMS, YOU AND WIFI-K9 ARE EACH WAIVING THE RIGHT TO A TRIAL BY JURY OR TO PARTICIPATE IN A CLASS ACTION.\n" +
                        "\t\t\tIf we can’t resolve your dispute, you agree to arbitration.\n" +
                        "20. Governing Law and Jurisdiction\t\t\t\n" +
                        "\tThe laws of the State of New York will govern these Terms, as well as any claim, cause of action or dispute that might arise between you and WiFi-K9 (a \"Claim\"), without regard to conflict of law provisions. For any claim brought by either party, you agree to submit and consent to the personal and exclusive jurisdiction in, and the exclusive venue of, the state and federal courts located within New York. The Services are controlled and operated from the United States, and we make no representations that they are appropriate or available for use in other locations.\t\t\tNew York State law governs this agreement.\n" +
                        "\n" +
                        "\n" +
                        "21. General Terms\t\t\t\n" +
                        "\ta.\tWe reserve the right to modify, update, or discontinue the Services or any portion of the Services at our sole discretion, at any time, for any or no reason, and without notice or liability. \n" +
                        "b.\tWe may, at our own discretion, provide you with notices, including those regarding changes to the Terms by email or communications through the Services. WiFi-K9 reserves the right to determine the form and means of providing notifications to you, and you agree to receive legal notices electronically.\n" +
                        "c.\tNothing herein is intended, nor will be deemed, to confer rights or remedies upon any third party.  \n" +
                        "d.\tThese Terms, together with the Privacy Policy and any amendments and any additional agreements you may enter into with WiFi-K9, contain the entire agreement between you and us regarding the use of the Services, and supersede any prior agreement between you and us on such subject matter. The parties acknowledge that no reliance is placed on any representation made but not expressly contained in these Terms. \n" +
                        "e.\tAny failure on WiFi-K9’s part to exercise or enforce any right or provision of the Terms does not constitute a waiver of such right or provision. The failure of either party to exercise in any respect any right provided for herein shall not be deemed a waiver of any further rights hereunder. No waiver of any term of these Terms shall be deemed a further or continuing waiver of such term or any other term.\n" +
                        "f.\tIf any provision of these Terms is found to be unenforceable or invalid, then only that provision shall be modified to reflect the parties' intention or eliminated to the minimum extent necessary so that these Terms shall otherwise remain in full force and effect and enforceable. \n" +
                        "g.\tThese Terms, and any rights or obligations hereunder, are not assignable, transferable or sub-licensable by you except with WiFi-K9’s prior written consent, but may be assigned or transferred by us without restriction. Any attempted assignment by you shall violate these Terms and be void. \n" +
                        "h.\tThe section titles in these Terms are for convenience only and have no legal or contractual effect. \t\t\tOther terms you should be aware of.\n" +
                        "\n")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                alertDialog.show();

            }
        });


        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(SignupByEmail.this).setTitle("Privacy Policy").setMessage("June 15, 2017\n" +
                        "WiFi-K9 Inc.’s Privacy Policy\n" +
                        "This Privacy Policy describes the information collected by WiFi-K9 Inc. (“WiFi-K9”, “us”, “we”), how that information may be used, with whom it may be shared, and your choices about such uses and disclosures. By visiting www.wifi-k9.com (the “Site”) or accessing or using the websites, mobile applications, products, and services provided by WiFi-K9 (together with the Site, the “Services”), you are accepting the practices described in this Privacy Policy. This Policy forms an integral part of the WiFi-K9 Terms of Use, which is hereby incorporated by reference. Any capitalized term used but not defined in this Privacy Policy will have the meaning defined in the Terms of Use.\n" +
                        "\n" +
                        "Please read this Privacy Policy carefully when using the Services.\n" +
                        "The Information WiFi-K9 May Collect\n" +
                        "1.\tInformation You Provide\n" +
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
                        "WiFi-K9 will occasionally update this Privacy Policy. When WiFi-K9 posts changes to this Privacy Policy, it will revise the date at the top of this Privacy Policy. WiFi-K9 recommends that you check the Site and Services from time to time to inform yourself of any changes in this Privacy Policy or any of WiFi-K9's other policies.\n")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                alertDialog.show();

            }
        });
/*
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(SignupByEmail.this).setTitle("Terms of Service").setMessage("WiFi-K9 Inc. Terms of Service\n" +
                        "These Terms of Service are effective as of June 15, 2017\n" +
                        "Welcome to WiFi-K9. The WiFi-K9 websites, mobile applications, products, and services are provided by WiFi-K9 Inc., a Delaware Public Benefit Corporation (“we”, “us”, “WiFi-K9”). Your use of the WiFi-K9 websites, mobile applications, products, and services (hereinafter collectively referred to the “Services”) is subject to these terms of use (these “Terms”).\n" +
                        "Lawyers tell us to say:\t\t\tWhich means:\n" +
                        "\t\t\t\n" +
                        "\tThese Terms govern your use of the Services and by using the Services, you agree to be bound by the Terms as well as our Privacy Policy, which is hereby incorporated into these Terms by reference.\t\t\tThis contract and our Privacy Statement cover your use of our service.\n" +
                        "1. Acceptance\t\t\t\n" +
                        "\tPLEASE READ THESE TERMS CAREFULLY. BY CREATING A WIFI-K9 ACCOUNT, OR DOWNLOADING, INSTALLING OR OTHERWISE ACCESSING OR USING THE SERVICES OR ANY PORTION THEREOF, YOU HEREBY ACKNOWLEDGE AND AGREE THAT YOU HAVE READ, UNDERSTOOD, AND AGREE TO BE BOUND BY THESE TERMS, INCLUDING THE PRIVACY POLICY.\t\t\tBy using WiFi-K9, you agree to this contract and our Privacy Policy.\n" +
                        "2. Changes to the Terms of Service\t\t\t\n" +
                        "\tWe may modify these Terms from time to time. The most current version of the Terms will be located on www.wifi-k9.com (the “Site”). You understand and agree that your access to or use of the Services is governed by these Terms effective at the time of your access to or use of the Services. If we make material changes to these Terms, we will notify you by email or by posting a notice on the Services prior to the effective date of the changes. We will also indicate at the top of this page the date that revisions were last made. You should revisit these Terms on a regular basis, as revised versions will be binding on you. Any such modification will be effective upon our posting of new Terms. You understand and agree that your continued access to or use of the Service after the effective date of modifications to the Terms indicates your acceptance of the modifications.\t\t\tWe might update the Terms of Service as we add new features or for other reasons. If you use WiFi-K9 after any update, you agree to those updated Terms of Service.\n" +
                        "3. Our License to You\t\t\t\n" +
                        "\tSubject to these Terms and our policies, we grant you a limited, non-exclusive, non-transferable, and revocable license to use the Services.\t\t\tYou can use WiFi-K9, but you have to follow our rules\n" +
                        "4. Translation\t\t\t\n" +
                        "\tWe may translate these Terms into other languages for your convenience. Nevertheless, the English version governs your relationship with WiFi-K9. To the extent any translated version of these Terms conflicts with the English version, the English version controls.\t\t\tWe speak English, so if these Terms are in another language we can’t be responsible for any translation errors.\n" +
                        "\n" +
                        "5. Use of Services\t\t\t\n" +
                        "\tEligibility\t\t\t\n" +
                        "\tYou must be 18 years of age or older to use the Services. By using the Services, you represent that you are 18 or older, and that you will not permit a minor under the age of 18 to use the Services, your account, or otherwise interact with the Services. WiFi-K9 will never knowingly solicit or accept personally identifiable information or other content from a user or visitor who WiFi-K9 knows is under 18 years of age. If WiFi-K9 discovers that a user under 18 years of age has created an account, WiFi-K9 will terminate the account and remove the information or other content.\t\t\tSorry, you must be at least 18 years old to use WiFi-K9. We don’t make the laws, we just follow them.\n" +
                        "\tAvailability of Services or any Portion of the Services\t\t\t\n" +
                        "\tThe Services or any portion of the Services, including any and all mobile apps and applications, may be modified, updated, interrupte¬¬d, suspended or discontinued at any time without notice or liability. You understand and agree that your use of Services is at your own risk.\n" +
                        "We will use reasonable efforts to make the mobile apps or applications that are part of the Services available at all times. However, you acknowledge that the speed and quality of the Services may vary, and the Services are subject to unavailability, including third party service failures; emergencies; transmission, equipment or network problems or limitations; signal strength; interference; and maintenance and repair, and may be refused, interrupted, limited or diminished. WiFi-K9 is not responsible for any failures to maintain the accuracy, quality, confidentiality, or security of your data, messages or pages whether or not related to interruptions or performance issues with the Services. \n" +
                        "You acknowledge that you may log into the Services using certain third party sites and services, including but not limited to Facebook and Google (the “Supported Platform(s)”), and that your ability to do so is highly dependent on the availability of such Supported Platforms. If at any time any Supported Platforms cease to make their programs available to WiFi-K9 on reasonable terms, WiFi-K9 may cease to provide such features to you without entitling you to refund, credit, or other compensation. In order to use the features of the Services related to the Supported Platforms, you may be required to register for or log into such Supported Platforms on their respective websites. By enabling such Supported Platforms within the Service, you are allowing WiFi-K9 to pass your log-in information to these Supported Platforms for this purpose.\t\t\tWe do our best, but sometimes things beyond our control happen.\n" +
                        "\tSystem Requirements for Applications\t\t\t\n" +
                        "\tIn order to use any mobile apps or applications that are part of the Services, you are required to have a compatible device, Internet access, and the necessary minimum specifications (the “System Requirements”). You hereby acknowledge that the System Requirements may change from time to time, without notice, and that WiFi-K9 makes no representations as to the accuracy of the System Requirements.\n" +
                        "Some of the Services may be software that is downloaded to your computer, phone, tablet, or other device. You agree that we may automatically upgrade those Services, and these Terms will apply to such upgrades. You may further be required to obtain software and/or hardware updates or upgrades from time to time, as may be necessary in order to continue to use of the Services. You hereby acknowledge and agree that obtaining and maintaining the applicable System Requirements remains your responsibility.\t\t\tYou might need to upgrade your mobile device or download updates in order to use WiFi-K9.\n" +
                        "6. Payment\t\t\t\n" +
                        "\tYou acknowledge that you are bound by the terms of agreement with your mobile network or Internet provider (collectively, the “Mobile Provider”), which will continue to apply when using the Services. As a result, you may be charged by the Mobile Provider for any such third-party charges as may arise. You hereby acknowledge that WiFi-K9 is not responsible for such charges or services. If you are not the authorized party to make payment for any Mobile Provider charges incurred to access the Services, you hereby warrant and represent that you have all necessary authority from such authorized party to use the Services.\t\t\tYou still have to pay any applicable mobile provider charges when you use WiFi-K9.\n" +
                        "7. User Accounts and User Information\t\t\t\n" +
                        "\tYou must create an account and provide certain information about yourself in order to use the Services. You are responsible for maintaining the confidentiality of your account password. You are also responsible for all activities that occur in connection with your account. You agree to notify us immediately of any unauthorized use of your account. We reserve the right to terminate or suspend your account at any time for any or no reason.\t\t\tDon’t share your account information with anyone else.\n" +
                        "\tIn creating your account, we ask that you provide complete, accurate, and up-to-date information. You may not create or use an account for anyone other than yourself, provide a name, address, or email address other than your own, or create multiple accounts.\t\t\tPlease tell us the truth about yourself.\n" +
                        "\n" +
                        "\tYou agree to use these Services only for purposes as laid out by the (a) Terms and (b) any applicable law. You agree that you are solely responsible for any breach of your obligations under these Terms and the consequences of any breach arising. You agree to the use of your data in accordance with WiFi-K9 Privacy Policy. You shall not engage in activities which interfere with or disrupt the Services or our servers or in any way in connection to the Services.\t\t\tYou’re responsible for what you do when you use WiFi-K9.\n" +
                        "\n" +
                        "8. Communications from WiFi-K9\t\t\t\n" +
                        "\tBy creating an account, you agree to receive certain communications in connection with the Services. For example, we may periodically send you emails, including but not limited to information related to your WiFi-K9 account.\t\t\tWe might email you important information.\n" +
                        "9. Advertising\t\t\t\n" +
                        "\tWiFi-K9 and its licensees may publicly display advertisements and other information within the Services. Such advertisements may be targeted to the information on the Services, your location, or any other information. You are not entitled to any compensation for such advertisements. The manner, mode and extent of such advertising are subject to change without specific notice to you.  The Services may contain links to other websites maintained by third parties. These links are provided solely as a convenience and do not imply endorsement of, or association with, the third parties by WiFi-K9.\t\t\tWiFi-K9 shows advertisements so that we can keep the service free.\n" +
                        "10. Hotspot Submissions and Other User Content\t\t\t\n" +
                        "\tThe Services may permit you and other users to submit locations where a public Internet connection may be available (each, a “Hotspot”). You are responsible for the Hotspot locations and any other information, images, photos, drawings, icons, text and/or other content (“Content”) that you provide or otherwise make available to the Services, including without limitation its legality, accuracy, reliability, and appropriateness.\t\t\tYou are responsible for any content you submit to us.\n" +
                        "\tYou authorize WiFi-K9 to publish the Hotspot information you submit so that it may be accessed by other users of the Services, and you waive any rights of attribution and/or credit for such Hotspot information. Following termination or deactivation of your account, or upon removal any Hotspot information from WiFi-K9, we may retain the Hotspot information you provide for backup, archival, or audit purposes. \t\t\tWe can use any content you submit to us.\n" +
                        "\tWe Do Not Review or Verify User-Submitted Hotspots On the Services\t\t\t\n" +
                        "\tThe Hotspot locations and any other content available through the Services has not been reviewed, verified or authenticated by WiFi-K9, and may include inaccuracies or false information or be offensive, indecent or objectionable. WiFi-K9 cannot make any representations, warranties, or guarantees in connection with the Services, including relating to the accuracy, quality, usability, or suitability of any Hotspot. Any Hotspot that is provided by a business, organization, or other third party is not endorsed by or associated with WiFi-K9 and vice versa. You hereby acknowledge and affirm that it is your sole responsibility for and assume all risk arising from your use or reliance of connecting to any Hotspot, whether while using the Services or not. If notified by a user or Hotspot owner that a Hotspot is allegedly inaccurate does not conform to the Terms, we may investigate the allegation and determine in our sole discretion whether to remove the Hotspot, which we reserve the right to do at any time and without notice.\t\t\tWe don’t verify any content submitted to us by other users.\n" +
                        "\tFeedback\t\t\t\n" +
                        "\tWe value hearing from our users, and are always interested in learning about ways we can improve the WiFi-K9 Services. If you choose to submit comments, ideas or feedback, you agree that we are free to use them without any restriction or compensation to you. By accepting your submission, WiFi-K9 does not waive any rights to use similar or related feedback previously known to WiFi-K9, or developed by its employees, or obtained from sources other than you.\t\t\tIf you give us ideas on how to improve WiFi-K9, we can use them.\n" +
                        "\n" +
                        "11. Security\t\t\t\n" +
                        "\tWe care about the data security and privacy of our users. While we work to maintain the security of your content and account, WiFi-K9 cannot guarantee that unauthorized third parties will not be able to defeat our security measures. Please notify us immediately of any compromise or unauthorized use of your account.\n" +
                        "\t\t\tWe do our best to keep WiFi-K9 secure, but that doesn’t mean someone won’t outsmart us.\n" +
                        "12. Third Parties\t\t\t\n" +
                        "\tThird Party Links, Sites, and Services\t\t\t\n" +
                        "\tThe Services may include content, information, and/or data from, or links to, other apps, web apps, websites, or services, including but not limited to the Supported Platforms (\"Third Party Content\"). We do not control, assume responsibility for or endorse any Third Party Content. You agree that we are not responsible for the availability or contents of such Third Party Content. Your use of Third Party Content is at your own risk and you agree that WiFi-K9 will have no liability arising from your use of or access to any Third Party Content. When you access the Third Party Content, you will do so at your own risk. Any use of Third Party Content is governed solely by the terms and conditions of such Third Party Content provider (and you shall comply with all such terms and conditions), and any contract entered into, or any transaction completed via any Third Party Content provider or Supported Platform, is between you and the relevant third party, and not WiFi-K9. WiFi-K9 makes no representation and shall have no liability or obligation whatsoever in relation to the content or use of, or correspondence with, any such Third Party Content or any transactions completed and any contract entered into by you with any such third party.\t\t\tWe can’t control what others do.\n" +
                        "\tThird Party Terms of Use\t\t\t\n" +
                        "\tThe Services may, from time to time, access Third Party Content by accessing third party Application Programing Interfaces (“APIs”) within the Services. You understand and agree that the Services are not endorsed, certified or otherwise approved in any way by the third party providing such API and the provider of the API is not responsible for the Services. Notwithstanding any license provided under these Terms (including the end user license granted under these Terms), (i) any such third party API is provided “as-is,” without any warranties and all implied warranties, including the implied warranties of merchantability, fitness for a  particular purpose and non-infringement, are expressly disclaimed; (ii) you may not modify or create derivative works based on any part of any such third party API; (iii) you may not decompile, reverse-engineer, disassemble, and/or otherwise reduce any such third party API to source code or other human-perceivable form, to the full extent allowed by law; (iv) ownership of any such third party API and any services related to any such third party API remain with the owner of the API; and (v) the provider of any third-party API used in connection with the Services disclaims any and all liability on the part of the third-party API provider for any interruption in its services as accessed via the Services.\t\t\tWe can’t control any other services that are incorporated into WiFi-K-9.\n" +
                        "13. Proprietary Rights\t\t\t\n" +
                        "\tYou acknowledge and agree that WiFi-K9 owns all legal right, title and interest in and to the Services, including any intellectual property rights which subsist in the Services (whether those rights happen to be registered or not, and wherever in the world those rights may exist), and that the Services may contain information which may be of confidential nature to WiFi-K9 and you have no right to disclose such information.\n" +
                        "WiFi-K9, the WiFi-K9 logo, and other WiFi-K9 trademarks, service marks, graphics and logos used in connection with the Services are trademarks or registered trademarks of WiFi-K9 Inc. (collectively “WiFi-K9 Marks”). Other trademarks, service marks, graphics and logos used in connection with the Services are the trademarks of their respective owners (collectively “Third Party Marks”). The WiFi-K9 Marks and Third Party Marks may not be copied, imitated or used, in whole or in part, without the prior written permission of WiFi-K9 or the applicable trademark holder. The Services and all Content contained therein (including Third Party Content) are protected by copyright, trademark, patent, trade secret, international treaties, laws and other proprietary rights, and may have security components that protect digital information only as authorized by WiFi-K9 or the owner of the Content.\n" +
                        "You may not use any of our domain names without prior written consent by WiFi-K9.  You will not use any trademark, service mark, trade name, and logo of any company or organization in a method that is likely to cause or intends to cause confusion about ownership and authority.  \t\t\tRespect our rights in our software and brand name.\n" +
                        "14. Restrictions and Guidelines\t\t\t\n" +
                        "\tWe are under no obligation to enforce the Terms on your behalf against another user. While we encourage you to let us know if you believe another user has violated the Terms, we reserve the right to investigate and take appropriate action at our sole discretion. \t\t\tDon’t use WiFi-K9 to break the law or violate anyone else’s rights.\n" +
                        "\tYou agree not to, and will not assist, encourage, or enable others to use the Services to, and will not on behalf of anyone else:\n" +
                        "•\tViolate any third party's rights, including any breach of confidence, copyright, trademark, patent, trade secret, moral right, privacy right, right of publicity, or any other intellectual property or proprietary right;\n" +
                        "•\tThreaten, stalk, harm, or harass others, or promote bigotry or discrimination; \n" +
                        "•\tSend bulk emails, surveys, or other mass messaging, whether commercial in nature or not, engage in keyword spamming, or otherwise attempt to manipulate the Services’ search results or any third party web applications or services;\n" +
                        "•\tSolicit personal information from minors; \n" +
                        "•\tMask any person’s identity for illegal or malicious purposes; or\n" +
                        "•\tViolate any applicable law.\n" +
                        "You also agree you will not, and will not assist, encourage, or enable any users or third parties to, and will not on behalf of anyone else:\n" +
                        "•\tViolate these Terms;\n" +
                        "•\tModify, adapt, appropriate, reproduce, distribute, translate, create derivative works or adaptations of, publicly display, sell, trade, or in any way exploit the Services, mobile app or application, or any Content within the Services (other than Content you provide), except as expressly authorized by WiFi-K9;\n" +
                        "•\tUse any robot, spider, search/retrieval application, or other automated device, process or means to access, retrieve, scrape, or index any portion of the Services;\n" +
                        "•\tReverse engineer any portion of the Services;\n" +
                        "•\tRemove or modify any copyright, trademark or other proprietary rights notice that appears on any portion of the Services or on any materials printed or copied from the Services;\n" +
                        "•\tResell, reproduce, duplicate, copy or trade the Services for any purpose;\n" +
                        "•\tAccess, retrieve or index any portion of the Services for purposes of constructing or populating a database;\n" +
                        "•\tReformat or frame any portion of the Services;\n" +
                        "•\tTake any action that imposes, or may impose, in our sole discretion, an unreasonable or disproportionately large load on Services' technology infrastructure or otherwise make excessive traffic demands of the Services;\n" +
                        "•\tAttempt to gain unauthorized access to the Services, user accounts, computer systems or networks connected to the Services through hacking, password mining or any other means;\n" +
                        "•\tUse the Services to transmit any computer viruses, worms, defects, Trojan horses or other items of a destructive nature (collectively, \"Viruses\");\n" +
                        "•\tUse any device, software or routine that interferes with the proper working of the Services, or otherwise attempt to interfere with the proper working of the Services;\n" +
                        "•\tUse the Services to violate the security of any computer network, crack passwords or security encryption codes; disrupt or interfere with the security of, or otherwise cause harm to, the Services or any portion thereof; \n" +
                        "•\tRemove, circumvent, disable, damage or otherwise interfere with any security-related features of the Services, features that prevent or restrict the use or copying of content, or features that enforce limitations on the use of the Services;\n" +
                        "•\tBreak or circumvent the Services’ security measures or otherwise test the vulnerability of our systems or networks;\n" +
                        "•\tAccess the Services by any other means other than the applications, mobile apps, and/or other software provided by WiFi-K9;\n" +
                        "•\tDo anything that violates applicable law or regulations;\n" +
                        "•\tShare your password, let anyone access your account, or do anything that might put your account at risk; or\n" +
                        "•\tSell your username or account or otherwise transfer it for compensation.\n" +
                        "You will at all times abide by all applicable local, state, national and foreign laws, treaties and regulations in connection with your use of the Services, including but not limited to those related to data privacy.\t\t\tDon’t do any of the things listed to the left.\n" +
                        "\tTerritorial Restrictions\t\t\t\n" +
                        "\tThe information and or features provided within the Services is not intended for distribution to or use by any person or entity in any jurisdiction or country where such distribution or use would be contrary to law or regulation or which would subject WiFi-K9 to any registration requirement within such jurisdiction or country. We reserve the right to limit the availability of our Services or any portion of the Services, to any person, geographic area, or jurisdiction, at any time and in our sole discretion, and to limit the quantities or features of any content, product, service or other feature that we provide. \n" +
                        "Services provided by WiFi-K9 may be subject to United States export controls. Thus, no software from the Service may be downloaded, exported or re-exported: (a) into (or to a national or resident of) any country to which the United States has embargoed goods; or (b) to anyone on the U.S. Treasury Department's list of Specially Designated Nationals or the U.S. Commerce Department's Table of Deny Orders. By downloading any software or applications related to the Services, you represent and warrant that you are not located in, under the control of, or a national or resident of, any such country or on any such list. \t\t\tDon’t use or download WiFi-K9 in a place where it would break the law.\n" +
                        "\n" +
                        "15. Termination\t\t\t\n" +
                        "\tWe reserve the right in our sole discretion to terminate, disable, or suspend your account, your access to or use of the Services or any portion thereof, inclusive of any mobile or other apps or applications software or to terminate your account and these Terms at any time, with or without notice.  If you breach any of the provisions of these Terms, your right to use the Services will automatically be terminated. You may terminate these Terms at any time, with or without notice, by deleting your account and discontinuing all access to and use of the Services. In the event that your account is terminated by you or us, you will promptly remove all copies of the Services or any portion thereof, inclusive of any mobile or other apps or applications software, from your possession and control. Upon termination of these Terms, any provision that by its nature or express terms should survive will survive such termination or expiration, including, but not limited to the terms contained in the sections labeled “Payment,” the license you grant in “Restrictions and Guidelines,” “Indemnification,” “Third Parties,” “Proprietary Rights,” “Disclaimer of Warranty and Limitation of Liability,” “General Terms,” and “Dispute Resolution and Arbitration”, and our Privacy Policy (for information provided during the duration of the Terms).\t\t\tWe can revoke your right to use WiFi-K9.\n" +
                        "16. Indemnification\t\t\t\n" +
                        "\tYou agree to indemnify, defend, and hold WiFi-K9, its parents, subsidiaries, affiliates, any related companies, suppliers, licensors and partners, and the officers, directors, employees, agents and representatives of each of them harmless, including costs, liabilities, claims, suits, proceedings, disputes, demands, liabilities, damages, losses, costs and expenses, and legal fees, from any claim or demand made by any third party arising out of or relating to (i) your access to or use of the Services or any portion thereof, (ii) your violation of these Terms, (iii) any products or services purchased or obtained by you in connection with the Services or any portion thereof, (iv) the infringement by you, or any third party using your account, of any intellectual property, privacy, or other right of any person or entity, or (v) any illegal, threatening, harmful, or harassing acts committed by you, or any third party using your account. WiFi-K9 reserves the right, at your expense, to assume the exclusive defense and control of any matter for which you are required to indemnify us and you agree to cooperate with our defense of these claims. You agree not to settle any such matter without the prior written consent of WiFi-K9. WiFi-K9 will use reasonable efforts to notify you of any such claim, action or proceeding upon becoming aware of it.\t\t\tIf we are sued because of your use of WiFi-K9, you’re responsible for our expenses.\n" +
                        "17. Disclaimer of Warranty and Limitation of Liability\t\t\t\n" +
                        "\tWiFi-K9 is not responsible for any Third Party Content, or any other Content on the Services, whether posted or caused by users of the Services, WiFi-K9, third parties or by any of the equipment or programming associated with or utilized in the Services, including but not limited to Hotspot listings. WiFi-K9 assumes no responsibility for any error, omission, interruption, deletion, defect, delay in operation or transmission, communications line failure, theft or destruction or unauthorized access to, or alteration of, user communications while using the Services.\t\t\tWe’re not responsible for anyone else’s content or services.\n" +
                        "\tYou understand that it is your duty to confirm and verify any information provided on or through the Site and Services, and that you bear the sole risk of relying on any such information, including but not limited to Content, Third-Party Content, Hotspot locations, or links. WiFi-K9 is not responsible for any problems or technical malfunction of any telephone network or lines, cellular data provider or network, computer online systems, servers or providers, computer equipment, software, Hotspot, or Supported Platforms on account of technical problems or traffic congestion on the Services or Site, including injury or damage to users or to any other person's computer, and/or mobile device.\t\t\tWe’re not responsible for content or services not provided by us.\n" +
                        "\tNeither WiFi-K9 nor any of its affiliates, advertisers, promoters or distribution partners shall be responsible for any loss or damage, including personal injury or death, resulting from anyone's use of the Services, any Content, use of any Hotspot, or any failure to maintain the confidentiality, security, accuracy or quality of your data, messages or pages.\t\t\tWe’re not responsible for anyone else’s behavior.\n" +
                        "\tWE TRY TO KEEP SERVICES UP, BUG-FREE, AND SAFE, BUT YOU USE THEM AT YOUR OWN RISK. THE INFORMATION FROM OR THROUGH THE SITE AND THE SERVICES IS PROVIDED \"AS IS,\" \"AS AVAILABLE,\" AND ALL WARRANTIES, EXPRESS OR IMPLIED, ARE DISCLAIMED. THE CONTENT AND THE SERVICES MAY CONTAIN VIRUSES, BUGS, ERRORS, PROBLEMS OR OTHER LIMITATIONS. IN NO EVENT WILL WIFI-K9 OR ITS DIRECTORS, OFFICERS, EMPLOYEES OR AGENTS HAVE ANY LIABILITY WHATSOEVER FOR YOUR USE OF ANY CONTENT OR SERVICES. WE ARE NOT LIABLE FOR ANY INDIRECT, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES (INCLUDING DAMAGES FOR LOSS OF BUSINESS, LOSS OF PROFITS, LITIGATION, OR THE LIKE), WHETHER BASED ON BREACH OF CONTRACT, BREACH OF WARRANTY, TORT (INCLUDING NEGLIGENCE), PRODUCT LIABILITY OR OTHERWISE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. NO ADVICE OR INFORMATION OBTAINED BY YOU FROM US THROUGH THE SERVICES SHALL CREATE ANY WARRANTY, REPRESENTATION OR GUARANTEE NOT EXPRESSLY STATED IN THIS AGREEMENT.\n" +
                        "WIFI-K9 DOES NOT CONTROL THE INFORMATION OR CONTENT PROVIDED BY THIRD PARTIES ON THE SERVICES, AND THEREFORE SHALL NOT BE RESPONSIBLE FOR YOUR RELIANCE ON ANY INFORMATION OR STATEMENTS MADE ON OR THROUGH THE SERVICES, INCLUDING BUT NOT LIMITED TO THROUGH ADVERTISEMENTS OR HOTSPOT LISTINGS. WIFI-K9 IS NOT RESPONSIBLE OR LIABLE IN ANY MANNER FOR ANY THIRD PARTY SERVICES, HOTSPOT, OR SUPPORTED PLATFORMS ASSOCIATED WITH OR UTILIZED IN CONNECTION WITH THE SERVICES, INCLUDING THE FAILURE OF ANY SUCH THIRD PARTY SERVICES, HOTSPOT OR SUPPORTED PLATFORMS.\t\t\tWe’re not responsible if something goes REALLY wrong. We do our best, but we can’t guarantee anything.\n" +
                        "\tWIFI-K9’S LIABILITY TO YOU FOR ANY CAUSE WHATSOEVER, AND REGARDLESS OF THE FORM OF THE ACTION, WILL AT ALL TIMES BE LIMITED TO THE FEES, IF ANY, PAID BY YOU TO US FOR THE SERVICES WITHIN THE PRIOR THREE (3) MONTHS, BUT IN NO CASE WILL OUR LIABILITY TO YOU EXCEED $50. YOU AGREE THAT DISPUTES BETWEEN YOU AND WIFI-K9 WILL BE RESOLVED BY BINDING, INDIVIDUAL ARBITRATION AND YOU WAIVE YOUR RIGHT TO PARTICIPATE IN A CLASS ACTION LAWSUIT OR CLASS-WIDE ARBITRATION. YOU ACKNOWLEDGE THAT IF NO FEES ARE PAID TO US FOR THE SITE AND THE SERVICES, YOU SHALL BE LIMITED TO INJUNCTIVE RELIEF ONLY, UNLESS OTHERWISE PERMITTED BY LAW, AND SHALL NOT BE ENTITLED TO DAMAGES OF ANY KIND FROM US, REGARDLESS OF THE CAUSE OF ACTION. IF YOU ARE A CALIFORNIA RESIDENT, YOU WAIVE CALIFORNIA CIVIL CODE SECTION 1542, WHICH STATES, IN PART: \"A GENERAL RELEASE DOES NOT EXTEND TO CLAIMS WHICH THE CREDITOR DOES NOT KNOW OR SUSPECT TO EXIST IN HIS FAVOR AT THE TIME OF EXECUTING THE RELEASE, WHICH IF KNOWN BY HIM MUST HAVE MATERIALLY AFFECTED HIS SETTLEMENT WITH THE DEBTOR\".\t\t\tOur liability to you is limited.\n" +
                        "18. Time Limitation on Claims\t\t\t\n" +
                        "\tYou agree that any claim you may have arising out of or related to your relationship with WiFi-K9 must be filed within one (1) year after such claim arose; otherwise, your claim is permanently barred.\t\t\tIf you have a dispute with us, you have one year to file a claim.\n" +
                        "19. Dispute Resolution and Arbitration\t\t\t\n" +
                        "\tFor any dispute you have with WiFi-K9, you agree to first contact us and attempt to resolve the dispute with us informally. You agree that, if WiFi-K9 has not been able to resolve the dispute with you informally, to resolve any claim, dispute, or controversy (excluding claims for injunctive or other equitable relief) arising out of or in connection with or relating to these Terms by binding arbitration by except for claims for injunctive or equitable relief or claims regarding intellectual property rights (which may be brought in any competent court without the posting of a bond), shall be finally settled in accordance with the Comprehensive Arbitration Rules of the Judicial Arbitration and Mediation Service, Inc. (“JAMS”) by arbitrators appointed in accordance with such rules. The award rendered by the arbitrator shall include costs of arbitration, reasonable attorneys' fees and reasonable costs for expert and other witnesses, and any judgment on the award rendered by the arbitrator may be entered in any court of competent jurisdiction. Nothing in this Section shall prevent either party from seeking injunctive or other equitable relief from the courts for matters related to data security, intellectual property or unauthorized access to the Service. ALL CLAIMS MUST BE BROUGHT IN THE PARTIES' INDIVIDUAL CAPACITY, AND NOT AS A PLAINTIFF OR CLASS MEMBER IN ANY PURPORTED CLASS OR REPRESENTATIVE PROCEEDING, AND, UNLESS WE AGREE OTHERWISE, THE ARBITRATOR MAY NOT CONSOLIDATE MORE THAN ONE PERSON'S CLAIMS. YOU AGREE THAT, BY ENTERING INTO THESE TERMS, YOU AND WIFI-K9 ARE EACH WAIVING THE RIGHT TO A TRIAL BY JURY OR TO PARTICIPATE IN A CLASS ACTION.\n" +
                        "\t\t\tIf we can’t resolve your dispute, you agree to arbitration.\n" +
                        "20. Governing Law and Jurisdiction\t\t\t\n" +
                        "\tThe laws of the State of New York will govern these Terms, as well as any claim, cause of action or dispute that might arise between you and WiFi-K9 (a \"Claim\"), without regard to conflict of law provisions. For any claim brought by either party, you agree to submit and consent to the personal and exclusive jurisdiction in, and the exclusive venue of, the state and federal courts located within New York. The Services are controlled and operated from the United States, and we make no representations that they are appropriate or available for use in other locations.\t\t\tNew York State law governs this agreement.\n" +
                        "\n" +
                        "\n" +
                        "21. General Terms\t\t\t\n" +
                        "\ta.\tWe reserve the right to modify, update, or discontinue the Services or any portion of the Services at our sole discretion, at any time, for any or no reason, and without notice or liability. \n" +
                        "b.\tWe may, at our own discretion, provide you with notices, including those regarding changes to the Terms by email or communications through the Services. WiFi-K9 reserves the right to determine the form and means of providing notifications to you, and you agree to receive legal notices electronically.\n" +
                        "c.\tNothing herein is intended, nor will be deemed, to confer rights or remedies upon any third party.  \n" +
                        "d.\tThese Terms, together with the Privacy Policy and any amendments and any additional agreements you may enter into with WiFi-K9, contain the entire agreement between you and us regarding the use of the Services, and supersede any prior agreement between you and us on such subject matter. The parties acknowledge that no reliance is placed on any representation made but not expressly contained in these Terms. \n" +
                        "e.\tAny failure on WiFi-K9’s part to exercise or enforce any right or provision of the Terms does not constitute a waiver of such right or provision. The failure of either party to exercise in any respect any right provided for herein shall not be deemed a waiver of any further rights hereunder. No waiver of any term of these Terms shall be deemed a further or continuing waiver of such term or any other term.\n" +
                        "f.\tIf any provision of these Terms is found to be unenforceable or invalid, then only that provision shall be modified to reflect the parties' intention or eliminated to the minimum extent necessary so that these Terms shall otherwise remain in full force and effect and enforceable. \n" +
                        "g.\tThese Terms, and any rights or obligations hereunder, are not assignable, transferable or sub-licensable by you except with WiFi-K9’s prior written consent, but may be assigned or transferred by us without restriction. Any attempted assignment by you shall violate these Terms and be void. \n" +
                        "h.\tThe section titles in these Terms are for convenience only and have no legal or contractual effect. \t\t\tOther terms you should be aware of.\n" +
                        "\n")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                alertDialog.show();
            }
        });

        */
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        api = ApiK9Server.getApiInterface(getApplicationContext());
    }



    private void setupCitizenshipInput() {
        ArrayAdapter<String> adapter = createCountiesStringAdapter();
        mCitizenship = (Spinner) findViewById(com.wxy.vpn.R.id.signup_citizenship);
        mCitizenship.setAdapter(adapter);
        mCitizenship.setSelection(adapter.getPosition(Locale.getDefault().getDisplayCountry()));
    }

    private void setupPasswordRepeatInput() {
        mPasswordRepeatView = (TextInputLayout) findViewById(com.wxy.vpn.R.id.signup_password_repeat_layout);
        mPasswordRepeatValidator = new GuiHelperUtils.PasswordValidator(mPasswordView, mPasswordRepeatView);
        mPasswordRepeatView.getEditText().addTextChangedListener(mPasswordRepeatValidator);
        mPasswordRepeatView.getEditText().setFilters(new InputFilter[]{mPasswordRepeatValidator});
    }

    private void setupPasswordInput() {
        mPasswordView = (TextInputLayout) findViewById(com.wxy.vpn.R.id.signup_password_layout);
        mPasswordValidator = new GuiHelperUtils.PasswordValidator(mPasswordView, ApiK9Server.PASSWORD_LENGTH);
        mPasswordView.getEditText().addTextChangedListener(mPasswordValidator);
        mPasswordView.getEditText().setFilters(new InputFilter[]{mPasswordValidator});
    }

    private void setupEmailInput() {
        mEmailView = (TextInputLayout) findViewById(com.wxy.vpn.R.id.signup_email_layout);
        mEmailValidator = new GuiHelperUtils.EmailValidator(mEmailView);
        mEmailView.getEditText().addTextChangedListener(mEmailValidator);
        mEmailView.getEditText().setFilters(new InputFilter[]{mEmailValidator});
    }

    private void setupFullnameInput() {
        mFullNameView = (TextInputLayout) findViewById(com.wxy.vpn.R.id.signup_full_name_layout);
        mRequiredFieldValidator = new GuiHelperUtils.RequiredFieldValidator(mFullNameView);
        mFullNameView.getEditText().addTextChangedListener(mRequiredFieldValidator);
    }

    private ArrayAdapter<String> createCountiesStringAdapter() {
        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<>();

        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }

        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
        return new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
    }

    private void attemptSignUp() {
        final String fullName = mFullNameView.getEditText().getText().toString().trim();
        final String email = mEmailView.getEditText().getText().toString();
        final String password = mPasswordView.getEditText().getText().toString();
        final String passwordRepeat = mPasswordRepeatView.getEditText().getText().toString();
        final String deviceId = UUID.randomUUID().toString();   // FIXME write it to shared

        mTerms.setError(null);
        mprivacy.setError(null);

        if (mRequiredFieldValidator.validate(fullName)
                && mEmailValidator.validate(email)
                && mPasswordValidator.validate(password)
                && mPasswordRepeatValidator.validate(passwordRepeat)
                && isCitizenshipSelected()
                && GuiHelperUtils.isTermsAccepted(mTerms)
                && GuiHelperUtils.isPrivacyAccepted(mprivacy)) {

            Call<ApiK9Server.ApiResponse> signup = api.signUp(
                    new ApiK9Server.SignupCred(
                            fullName,
                            email,
                            password,
                            deviceId,
                            "03318058848"
                    )
            );

            mProgressDialog.setMessage("Processing…");
            mProgressDialog.show();

            signup.enqueue(this);
        }
    }

    private boolean isCitizenshipSelected() {
        if (mCitizenship.getSelectedItemId() == AdapterView.INVALID_ROW_ID) {
            Snackbar.make(
                    mCitizenship,
                    "Please select your country.",
                    Snackbar.LENGTH_SHORT
            ).show();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case com.wxy.vpn.R.id.btn_register:
                if (Connectivity.isConnectedWifi(this) || (Connectivity.isConnectedMobile(this))) {
                    attemptSignUp();
                    break;
                } else {
                    Toast.makeText(this, "Network Error!",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case com.wxy.vpn.R.id.snackbar_action:
                final Intent intent = new Intent(this, Login.class);
                intent.putExtra(EMAIL_FOR_LOGIN, mEmailView.getEditText().getText().toString().trim());
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onResponse(Call<ApiK9Server.ApiResponse> call, Response<ApiK9Server.ApiResponse> response) {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();

        if (!response.isSuccessful() && response.errorBody() != null) {
            ApiK9Server.ApiError error = ApiK9Server.parseError(response);
            for (String errorMessage : error.getErrors().getMessages()) {
                Snackbar.make(
                        findViewById(com.wxy.vpn.R.id.сoordinator_activity_signup_by_email),
                        errorMessage,
                        Snackbar.LENGTH_INDEFINITE
                ).show();
            }
            return;
        }

        ApiK9Server.ApiResponse decodedResponse = response.body();
        if (decodedResponse != null)
            Snackbar.make(
                    findViewById(com.wxy.vpn.R.id.сoordinator_activity_signup_by_email),
                    decodedResponse.getMessage(),
                    Snackbar.LENGTH_SHORT
            ).setAction("Login", this).show();
    }

    @Override
    public void onFailure(Call<ApiK9Server.ApiResponse> call, Throwable t) {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();

        Log.e(TAG, "Error while signup", t);
    }
}
