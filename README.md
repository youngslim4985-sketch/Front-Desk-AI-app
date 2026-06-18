Tech Stack

- Kotlin
- Android Studio
- Gradle
- Anthropic Claude API
- Claude-powered conversational intelligence

Run Locally

Prerequisites

- Android Studio
- Anthropic API Key
- Android emulator or physical Android device

Setup

1. Clone the repository:

git clone https://github.com/youngslim4985-sketch/Front-Desk-AI-app.git
cd Front-Desk-AI-app

2. Open the project in Android Studio.

3. Create a ".env" file in the project root.

4. Add your Claude API key:

ANTHROPIC_API_KEY=your_api_key_here

5. Configure the application to use the Anthropic API endpoint:

https://api.anthropic.com/v1/messages

6. If needed, remove this line from the app "build.gradle.kts" file:

signingConfig = signingConfigs.getByName("debugConfig")

7. Run the application.

AI Engine

Front-Desk AI is powered by Anthropic Claude and is designed to:

- Understand customer intent
- Schedule appointments
- Answer business questions
- Summarize conversations
- Generate AI call notes
- Capture lead information
- Escalate complex requests to human staff

Product Vision

Front-Desk AI serves as an AI employee for small businesses, providing:

- 24/7 customer support
- Intelligent call handling
- Appointment scheduling
- Lead qualification
- CRM automation
- Customer preference tracking
- Revenue recovery from missed calls
- Business intelligence dashboards<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/5b14a9d3-1d35-45f6-9690-20b650141fda

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device
