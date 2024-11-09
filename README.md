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
``` IntegratedChat/ ├── app/ # Main app module ├── data/ # Data and API handling module ├── domain/ # Business logic and data processing ├── infrastructure/ # Integration configurations with external services ├── ... # Additional modules as needed ```


  <h2>🧪 Testing</h2>
    <ul>
      <li><strong>Unit Tests</strong>: Written with JUnit4, MockK, and ShouldBe to ensure component functionality in isolation.</li>
      <li><strong>Manual Tests</strong>: Exploratory and functional testing conducted with QA collaboration.</li>
    </ul>
  <h2>📦 Installation and Execution</h2>
    <ol>
      <li><p><strong>Clone the repository</strong>:</p><pre class="!overflow-visible"><div class="contain-inline-size rounded-md border-[0.5px] border-token-border-medium relative bg-token-sidebar-surface-primary dark:bg-gray-950"><div class="flex items-center text-token-text-secondary px-4 py-2 text-xs font-sans justify-between rounded-t-md h-9 bg-token-sidebar-surface-primary dark:bg-token-main-surface-secondary select-none">bash</div><div class="sticky top-9 md:top-[5.75rem]"><div class="absolute bottom-0 right-2 flex h-9 items-center"><div class="flex items-center rounded bg-token-sidebar-surface-primary px-2 font-sans text-xs text-token-text-secondary dark:bg-token-main-surface-secondary"><span class="" data-state="closed"><button class="flex gap-1 items-center select-none py-1"><svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="icon-sm"><path fill-rule="evenodd" clip-rule="evenodd" d="M7 5C7 3.34315 8.34315 2 10 2H19C20.6569 2 22 3.34315 22 5V14C22 15.6569 20.6569 17 19 17H17V19C17 20.6569 15.6569 22 14 22H5C3.34315 22 2 20.6569 2 19V10C2 8.34315 3.34315 7 5 7H7V5ZM9 7H14C15.6569 7 17 8.34315 17 10V15H19C19.5523 15 20 14.5523 20 14V5C20 4.44772 19.5523 4 19 4H10C9.44772 4 9 4.44772 9 5V7ZM5 9C4.44772 9 4 9.44772 4 10V19C4 19.5523 4.44772 20 5 20H14C14.5523 20 15 19.5523 15 19V10C15 9.44772 14.5523 9 14 9H5Z" fill="currentColor"></path></svg>Copy code</button></span></div></div></div><div class="overflow-y-auto p-4" dir="ltr"><code class="!whitespace-pre hljs language-bash">git <span class="hljs-built_in">clone</span> https://github.com/jhoglassx/IntegratedChat.git
</code></div></div></pre></li><li><p><strong>Set up keys</strong>:</p><ul><li>Create a <code>keys-debug.properties</code> file in the root directory and add your credentials for the Twitch and YouTube APIs.</li></ul></li><li><p><strong>Run the project</strong>:</p><ul><li><strong>Android</strong>: Use Android Studio to build and run on a device or emulator.</li><li><strong>Desktop</strong>: Use IntelliJ IDEA for the desktop configuration or compile with Gradle.</li></ul></li></ol><h2>🌐 API and Integrations</h2><ul><li><strong>Twitch API</strong>: Integration for fetching and displaying live chat from Twitch.</li><li><strong>YouTube API</strong>: Integration for fetching and displaying live chat from YouTube.</li></ul><h2>💻 CI/CD</h2><p>The project uses <strong>GitHub Actions</strong> for CI/CD, with configured steps for build, unit and instrumentation tests, and test coverage report generation.</p><h2>📝 License</h2><p>This project is for educational and skills demonstration purposes and is not intended for production use.</p><hr><p>This README provides a detailed overview of the project and the technologies used, highlighting your experience and the project’s purpose. If you need any further customization, feel free to let me know!</p></div>
