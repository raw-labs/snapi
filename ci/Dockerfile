# We make an explicit base layer
# This can be copy pasted for most projects
FROM --platform=amd64 debian:bookworm AS base

ARG JAVA_VERSION="21.0.1-graalce"
ARG SBT_VERSION="1.9.6"

SHELL ["/bin/bash", "-o", "pipefail", "-c"]

# To avoid sbt yelling about us running in /root
WORKDIR /root/workdir
# This layer might not change often
RUN set -ex \
  && apt-get update \
  && apt-get install -y \
    curl \
    unzip \
    zip \
  && rm -rf /var/lib/apt/lists/* \
  && curl -s -S "https://get.sdkman.io?rcupdate=false" | bash \
  && sed -i 's/sdkman_auto_answer=false/sdkman_auto_answer=true/g'                "$HOME/.sdkman/etc/config" \
  && sed -i 's/sdkman_selfupdate_feature=true/sdkman_selfupdate_feature=false/g'  "$HOME/.sdkman/etc/config" \
  && sed -i 's/sdkman_colour_enable=true/sdkman_colour_enable=false/g'            "$HOME/.sdkman/etc/config"

# This one might change a bit more often
RUN set -ex \
  && source "$HOME/.sdkman/bin/sdkman-init.sh" \
  && sdk install sbt ${SBT_VERSION} \
  && sdk install java ${JAVA_VERSION}

ENV BASH_ENV="\$HOME/.sdkman/bin/sdkman-init.sh"

FROM base AS final

# Copy project source code to the container
COPY . .

ENTRYPOINT ["/bin/bash", "-c"]
