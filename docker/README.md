<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at
  http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

# hop-docker

A **Hop Docker image** supporting both **short-lived** and **long-lived** setups.

## Container Folder Structure

Directory    | Description
---	|---
`/opt/hop`    | location of the hop package
`/files`    | here you should mount a directory that contains the **hop and project config** as well as the **workflows
and pipelines**.

## Environment Variables

You can provide values for the following environment variables:

Environment Variable    | Required    | Description
---	|----	|---
`HOP_LOG_LEVEL`    | No    | Specify the log level. Default: `Basic`. Optional.
`HOP_FILE_PATH`    | Yes    | Path to hop workflow or pipeline
`HOP_LOG_PATH`    | No    | File path to hop log file
`HOP_CONFIG_DIRECTORY`    | No    | Path to the Hop config folder. DISABLED for now.
`HOP_PROJECT_NAME`    | Yes    | Name of the Hop project to use
`HOP_PROJECT_DIRECTORY`    | Yes    | Path to the home of the hop project. Should start with `/files`.
`HOP_PROJECT_CONFIG_FILE_NAME`    | No    | Name of the project config file including file extension. Defaults to `project-config.json`.
`HOP_ENVIRONMENT_NAME`    | Yes    | Name of the Hop run environment to use
`HOP_ENVIRONMENT_CONFIG_FILE_NAME_PATHS`    | Yes    | comma separated list of paths to environment config files (including filename and file extension). paths should start with `/files`.
`HOP_RUN_CONFIG`    | Yes    | Name of the Hop run configuration to use
`HOP_RUN_PARAMETERS`    | No    | Parameters that should be passed on to the hop-run command. Specify as comma separated list, e.g. `PARAM_1=aaa,PARAM_2=bbb`. Optional.
`HOP_OPTIONS`    | No    | Any JRE options you want to set
`HOP_SHARED_JDBC_DIRECTORY`    | No    | Path to the directory where the JDCB drivers are located
`HOP_SERVER_USER`    | No    | Username for hop-server, only valid in long-lived containers. Default `cluster`
`HOP_SERVER_PASS`    | No    | Password for hop-server user, only valid in long-lived containers. Default `cluster`
`HOP_CUSTOM_ENTRYPOINT_EXTENSION_SHELL_FILE_PATH` | No | Path to custom entrypoint extension script file, e.g. to fetch Hop project files from S3 or gitlab.

The `Required` column relates to running a short-lived container.

## How to run the Container

The most common use case will be that you run a **short-lived container** to just complete one Hop workflow or pipeline.

Example for running a **workflow**:

```bash
docker pull docker.io/apache/incubator-hop:<tag>
docker run -it --rm \
  --env HOP_LOG_LEVEL=Basic \
  --env HOP_FILE_PATH='${PROJECT_HOME}/pipelines-and-workflows/main.hwf' \
  --env HOP_PROJECT_DIRECTORY=/files/project \
  --env HOP_PROJECT_NAME=project-a \
  --env HOP_ENVIRONMENT_NAME=project-a-test \
  --env HOP_ENVIRONMENT_CONFIG_FILE_NAME_PATHS=/files/config/project-a-test.json \
  --env HOP_RUN_CONFIG=classic \
  --env HOP_RUN_PARAMETERS=PARAM_LOG_MESSAGE=Hello,PARAM_WAIT_FOR_X_MINUTES=1 \
  -v /path/to/local/dir:/files \
  --name my-simple-hop-container \
  apache/incubator-hop:<tag>
```

If you need a **long-lived container**, this option is also available. Run this command to start a Hop Server in a
docker container:

```bash
docker pull docker.io/apache/incubator-hop:<tag>
docker run -it --rm \
  --env HOP_SERVER_USER=admin \
  --env HOP_SERVER_PASS=admin \
  -p 8080:8080 \
  --name my-hop-server-container \
 apache/incubator-hop:<tag>
```

Hop Server is designed to receive all variables and metadata from executing clients. This means it needs little to no
configuration to run.

You can then access the hop-server UI from your host at `http://localhost:8080`

## Custom Entrypoint Extension Shell Script

To make the Hop Docker image even more flexible, we added a `HOP_CUSTOM_ENTRYPOINT_EXTENSION_SHELL_FILE_PATH` variable
that accepts a path to a custom shell script (that you provide). This shell script will run when you start the container
before your Hop project is registered with the container's Hop config and before your Hop workflow or pipeline gets
kicked off. This feature might come in handy when you want to run some custom logic upfront, e.g. source Hop project
files from S3 or clone them from GitHub.

The custom shell file can be provided in several ways (this is not a full list):

- via the mount point (`/files`)
- You create your own Dockerfile, define this image as the base and then use the `COPY` instruction to copy your custom
  shell file in your Docker image.

For the last scenario mentioned, it could be something like this:

We create a simple **bash script** called `clone-git-repo.sh` in a sub-folder called `resources`:

```shell
#!/bin/bash
cd /home/hop
git clone ${GIT_REPO_URI}
chown -R hop:hop /home/hop/${GIT_REPO_NAME}
```

We also make it parameter-driven, so it any other team can use it. We create our custom Dockerfile like so:

```dockerfile
FROM apache/incubator-hop:0.70-SNAPSHOT
ENV GIT_REPO_URI=
# example value: https://github.com/diethardsteiner/apache-hop-minimal-project.git
ENV GIT_REPO_NAME=
# example value: apache-hop-minimal-project
USER root
RUN apk update \
  && apk add --no-cache git  
# copy custom entrypoint extension shell script
COPY --chown=hop:hop ./resources/clone-git-repo.sh /home/hop/clone-git-repo.sh
USER hop
```

Note that apart from defining the new environment variables (that go in line with the parameters we defined in
the `clone-git-repo.sh` earlier on ), we also `COPY` the `clone-git-repo.sh` file to user hop's home directory.

Next let's build a small script which builds our custom image and then tests it by spinning up a container and running a
workflow:

```shell
#!/bin/zsh

DOCKER_IMG_CHECK=$(docker images | grep ds/custom-hop)

if [ ! -z "${DOCKER_IMG_CHECK}" ]; then
  echo "removing existing ds/custom-hop image"
  docker rmi ds/custom-hop:latest
fi

docker build . -f custom.Dockerfile -t ds/custom-hop:latest

echo " ==== TESTING ====="


HOP_DOCKER_IMAGE=ds/custom-hop:latest
PROJECT_DEPLOYMENT_DIR=/home/hop/apache-hop-minimal-project

docker run -it --rm \
  --env HOP_LOG_LEVEL=Basic \
  --env HOP_FILE_PATH='${PROJECT_HOME}/main.hwf' \
  --env HOP_PROJECT_DIRECTORY=${PROJECT_DEPLOYMENT_DIR} \
  --env HOP_PROJECT_NAME=apache-hop-minimum-project \
  --env HOP_ENVIRONMENT_NAME=dev \
  --env HOP_ENVIRONMENT_CONFIG_FILE_NAME_PATHS=${PROJECT_DEPLOYMENT_DIR}/dev-config.json \
  --env HOP_RUN_CONFIG=local \
  --env HOP_CUSTOM_ENTRYPOINT_EXTENSION_SHELL_FILE_PATH=/home/hop/clone-git-repo.sh \
  --env GIT_REPO_URI=https://github.com/diethardsteiner/apache-hop-minimal-project.git \
  --env GIT_REPO_NAME=apache-hop-minimal-project \
  --name my-simple-hop-container \
  ${HOP_DOCKER_IMAGE}
```

# Shortcomings

Currently the `hop-server` support is minimal.

