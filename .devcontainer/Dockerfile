FROM mcr.microsoft.com/devcontainers/java:1-21-bookworm@sha256:0af991049cded4b8d5a90d2614289ceec390779ad6a3e4b70957e2b4f3bb1230

# El laboratorio no utiliza Yarn. La fuente heredada de esta imagen tiene
# una clave APT ausente. Retiramos SOLO esa fuente ajena al proyecto;
# se mantiene la verificación criptográfica de los paquetes del escritorio.
RUN rm -f /etc/apt/sources.list.d/yarn.list /etc/apt/sources.list.d/yarn.sources
