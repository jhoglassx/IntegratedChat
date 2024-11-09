<div class="markdown prose w-full break-words dark:prose-invert dark">
  <h1>IntegratedChat</h1>
  <p>A study project to showcase skills and knowledge in developing applications with <strong>Kotlin Multiplatform (KMP)</strong>, <strong>Compose</strong>, <strong>Ktor</strong>, <strong>Koin</strong>, and <strong>MVI</strong>, integrating live chat from <strong>Twitch</strong> and <strong>YouTube</strong> platforms into a unified interface.</p>
  <h2>📋 Description</h2>
    <p>
      <strong>IntegratedChat</strong> is a cross-platform application designed for Android and Desktop. This project consolidates live chats from Twitch and YouTube into a unified interface, with a design inspired by both platforms but featuring a unique, unified look.
    </p>
  <h2>🎯 Objective</h2>
    <p>
      This project aims to demonstrate the capability to develop complex cross-platform applications using KMP, implementing various libraries and modern development patterns, such as MVI and dependency injection with Koin. It serves as a practical study to deepen knowledge in Android and Desktop development.
    </p>
  <h2>🚀 Features</h2>
    <ul>
      <li><strong>Live Chat Integration</strong>: Consolidation of Twitch and YouTube live chats.</li>
      <li><strong>Unified Interface</strong>: A custom design inspired by both platforms.</li>
      <li><strong>Cross-Platform</strong>: Supports Android and Desktop through Kotlin Multiplatform.</li>
      <li><strong>Architecture</strong>: Uses MVI pattern for effective state management.</li>
      <li><strong>Dependency Injection</strong>: Configured with Koin.</li>
    </ul>
  <h2>🛠️ Technologies and Tools</h2>
    <ul>
      <li><strong>Kotlin Multiplatform</strong>: Cross-platform base structure.</li>
      <li><strong>Compose (Jetpack Compose and Compose for Desktop)</strong>: Declarative UI for Android and Desktop.</li>
      <li><strong>Ktor</strong>: For communication with Twitch and YouTube APIs.</li>
      <li><strong>Kotlinx.serialization</strong>: For data serialization.</li>
      <li><strong>Koin</strong>: Dependency injection framework.</li>
      <li><strong>JUnit4, MockK, and ShouldBe</strong>: Tools for unit testing.</li>
      <li><strong>GitHub Actions</strong>: CI/CD for build automation and testing.</li>
    </ul>
  <h2>📂 Project Structure</h2>
<pre><code>
IntegratedChat/
├── app/                # Main app module
├── data/               # Data and API handling module
├── domain/             # Business logic and data processing
├── infrastructure/     # Integration configurations with external services
├── ...                 # Additional modules as needed
</code></pre>
  <h2>🧩 Principles and Architecture</h2>
  <p>This project follows the principles of <strong>Clean Code</strong>, <strong>Clean Architecture</strong>, and <strong>SOLID</strong> to ensure code readability, maintainability, and scalability:</p>
  <ul>
    <li><strong>Clean Code</strong>: Emphasis on readability, simplicity, and consistent coding practices.</li>
    <li><strong>Clean Architecture</strong>: Separation of concerns, enabling modular and testable code.</li>
    <li><strong>SOLID</strong>: Application of SOLID principles (Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion) to ensure a flexible and scalable architecture.</li>
  </ul>
  
  <h2>🧪 Testing</h2>
  <ul>
    <li><strong>Unit Tests</strong>: Written with JUnit4, MockK, and ShouldBe to ensure component functionality in isolation.</li>
    <li><strong>Manual Tests</strong>: Exploratory and functional testing conducted with QA collaboration.</li>
  </ul>
  <h2>📦 Installation and Execution</h2>
  <ol>
    <li><p><strong>Clone the repository</strong>:</p>
      <pre><code>git clone https://github.com/jhoglassx/IntegratedChat.git</code></pre>
    </li>
    <li>
      <p><strong>Set up keys</strong>:</p>
      <ul>
        <li>Create a <code>keys-debug.properties</code> file in the root directory and add your credentials for the Twitch and YouTube APIs.</li>
      </ul>
    </li>
    <li>
      <p><strong>Run the project</strong>:</p>
      <ul>
        <li><strong>Android</strong>: Use Android Studio to build and run on a device or emulator.</li>
        <li><strong>Desktop</strong>: Use IntelliJ IDEA for the desktop configuration or compile with Gradle.</li>
      </ul>
    </li>
  </ol>

  <h2>🌐 API and Integrations</h2>
  <ul>
    <li><strong>Twitch API</strong>: Integration for fetching and displaying live chat from Twitch.</li>
    <li><strong>YouTube API</strong>: Integration for fetching and displaying live chat from YouTube.</li>
  </ul>
  <h2>💻 CI/CD</h2>
  <p>The project uses <strong>GitHub Actions</strong> for CI/CD, with configured steps for build, unit and instrumentation tests, and test coverage report generation.</p>
  <h2>📝 License</h2>
  <p>This project is for educational and skills demonstration purposes and is not intended for production use.</p>
</div>
