# Sandbox Image for the Nexus 10-minute Workshop.
# Build context is workshops/nexus-10min/ so this can COPY exercise/ and solution/.
#
#   cd workshops/nexus-10min
#   docker buildx build --platform linux/amd64 -f sandbox/Dockerfile \
#     -t ghcr.io/nadvolod/devdays-kotlin-nexus-10min-sandbox:latest --push .
#   docker buildx imagetools inspect ghcr.io/nadvolod/devdays-kotlin-nexus-10min-sandbox:latest \
#     --format "{{.Manifest.Digest}}"
#   # paste that digest into instruqt/config.yml, then commit
#
# linux/amd64 is required. An arm64 image (the Apple Silicon default) fails to
# start on Instruqt with an empty log and no error. Do not pipe the build into
# anything: the pipe swallows a failed build's exit code.

FROM eclipse-temurin:21-jdk-jammy

RUN apt-get update && apt-get install -y --no-install-recommends \
      curl wget git vim nano less unzip ca-certificates bash-completion jq procps tmux \
    && rm -rf /var/lib/apt/lists/*

# --- Temporal CLI -------------------------------------------------------------
ARG TEMPORAL_CLI_VERSION=1.7.2
RUN curl -sSL \
      "https://github.com/temporalio/cli/releases/download/v${TEMPORAL_CLI_VERSION}/temporal_cli_${TEMPORAL_CLI_VERSION}_linux_amd64.tar.gz" \
    | tar -xz -C /usr/local/bin temporal \
    && chmod +x /usr/local/bin/temporal

# --- code-server (VS Code in the browser) -------------------------------------
# mathiasfrohlich.kotlin is syntax highlighting only: no JVM language server, so
# no Gradle indexing wait and no extra JVM competing with the Workers.
# Consequence: no autocomplete and no error squiggles. Assignment text must not
# tell learners to rely on either.
ARG CODE_SERVER_VERSION=4.96.4
RUN curl -fsSL https://code-server.dev/install.sh | sh -s -- --version ${CODE_SERVER_VERSION}
RUN code-server --extensions-dir /root/.cs-extensions \
      --install-extension mathiasfrohlich.kotlin

# security.workspace.trust.enabled MUST be false. Workspace Trust is on by
# default, and code-server renders a blank page until the folder is trusted.
RUN mkdir -p /root/.cs/User && printf '%s\n' \
      '{' \
      '    "security.workspace.trust.enabled": false,' \
      '    "workbench.colorTheme": "Default Dark+",' \
      '    "workbench.startupEditor": "none",' \
      '    "editor.fontSize": 14,' \
      '    "editor.tabSize": 4,' \
      '    "telemetry.telemetryLevel": "off"' \
      '}' > /root/.cs/User/settings.json

# --- Architecture diagram -----------------------------------------------------
# Served by jwebserver, which ships with the JDK. Lives outside the challenge
# folders: Instruqt parses every file in a challenge directory as a script.
COPY diagrams/ /opt/diagrams/

# --- Bake the workshop source -------------------------------------------------
# exercise/ and solution/ each carry their own Gradle wrapper. /opt/workshop is
# the pristine copy; track setup stages a writable one at /root/workshop.
COPY exercise/ /opt/workshop/exercise/
COPY solution/ /opt/workshop/solution/

# --- Pre-warm Gradle ----------------------------------------------------------
# Downloads the Gradle distribution and every dependency the lab needs, so the
# first ./gradlew is fast and the solve scripts can run --offline.
#
# The Solution runs `test`, not `compileKotlin`. Compiling resolves only the
# compile classpath; running a Worker needs the runtime classpath too (Jackson's
# jsr310 module, for one), and --offline then fails with "No cached version".
# The tests resolve it, and prove the Solution in the image while they are at it.
# Both trees share one dependency set, so the Exercise only needs to compile.
WORKDIR /opt/workshop/solution
RUN ./gradlew --no-daemon test

WORKDIR /opt/workshop/exercise
RUN ./gradlew --no-daemon compileKotlin

# Drop build output. The Gradle caches in /root/.gradle are what we keep.
RUN rm -rf /opt/workshop/solution/build /opt/workshop/exercise/build \
           /opt/workshop/solution/.gradle /opt/workshop/exercise/.gradle \
           /opt/workshop/solution/.kotlin /opt/workshop/exercise/.kotlin

# --- Shell environment --------------------------------------------------------
RUN echo 'export TEMPORAL_ADDRESS="${TEMPORAL_ADDRESS:-127.0.0.1:7233}"' >> /root/.bashrc

WORKDIR /root
CMD ["/bin/bash"]
