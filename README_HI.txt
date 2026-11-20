MAYA AI Android starter project

महत्वपूर्ण:
- यह अभी शुरुआती Android Studio project/source code है, तैयार APK नहीं।
- इस वातावरण में Android SDK/Gradle उपलब्ध नहीं था, इसलिए APK compile नहीं की गई।
- Android Studio में इस folder को खोलकर Gradle sync करें और Build > Build APK(s) चुनें।
- Payment reminder के लिए ऐप में ENABLE PAYMENT REMINDER दबाएँ और Android Accessibility Settings में MAYA AI को स्वयं सक्षम करें।
- Accessibility अनुमति संवेदनशील होती है। यह सेवा केवल Google Pay/PhonePe का foreground package पहचानती है; स्क्रीन का टेक्स्ट नहीं पढ़ती और कोई payment action नहीं करती।
- माइक्रोफोन अनुमति मिलने पर TAP TO SPEAK दबाकर हिंदी commands दें: “MAYA बंद हो जाओ”, “MAYA चालू हो जाओ”, “PhonePe खोलो”, “Google Pay खोलो”.
- यह prototype Gemini/Claude API, पूर्ण agent automation या always-on background hotword अभी लागू नहीं करता।
- API keys को ऐप के source code में hard-code न करें।

APK banane ka aasaan online tareeka:
1. ZIP ko extract karke project ko apne GitHub repository mein upload karein.
2. GitHub repository mein Actions tab kholein.
3. "Build MAYA AI APK" workflow select karke Run workflow dabayein.
4. Build complete hone par workflow run ke Artifacts section se MAYA-AI-debug-apk download karein.
5. APK ko apne Vivo phone par bhejkar install karein. Android unknown-source install permission maang sakta hai.
Is workflow ke liye GitHub account aur internet zaroori hai.

ANIVARYA PRIVACY POLICY:
SECURITY_POLICY_HI.txt padhein. MAYA AI OTP, PIN, password ya payment/banking sensitive details ko kabhi read aloud, extract, save ya transmit nahi karega. Current prototype kisi SMS ko read nahi karta.
