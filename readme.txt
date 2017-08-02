Please note that, in order for this application to run properly, an API key is required.

In order to procure one:
1. Create an account at www.themoviedb.org
2. Request an API Key
3. State that your app will be for educational/non-commercial use

For furtherinformation, please visit:
https://www.themoviedb.org/faq/api?language=en

Upon receipt of your API KEY, please make the following change in the application's code:
In package "Utilities", inside class "NetworkUtilities.java", line 30,
change the value of private static final String API_KEY
from "YOUR KEY HERE!!!" to your key.