FROM google/appengine-java

RUN /usr/bin/apt-get -q -y --force-yes update; apt-get --quiet --yes install -- openssl wget build-essential xorg libssl-dev xvfb libicu48 unzip imagemagick
RUN wget -q http://storage.googleapis.com/edu2bits.appspot.com/managed_vm.zip -P /download
RUN unzip -q /download/managed_vm.zip -d /download

RUN dpkg -i /download/wkhtmltox-0.12.1_linux-wheezy-amd64.deb

RUN wget --quiet https://download-installer.cdn.mozilla.net/pub/firefox/releases/29.0.1/linux-x86_64/en-US/firefox-29.0.1.tar.bz2 -P /download
RUN tar -jxvf /download/firefox-29.0.1.tar.bz2 -C /opt
RUN ln -s /opt/firefox/firefox /usr/bin/firefox
RUN mkdir images

RUN cp /download/firefox/local-settings.js /opt/firefox/defaults/pref/local-settings.js
RUN cp /download/firefox/mozilla.cfg /opt/firefox/mozilla.cfg

RUN tar -xf /download/flash/install_flash_player_11_linux.x86_64.tar.gz -C /download/flash/
RUN mkdir /usr/lib/mozilla
RUN mkdir /usr/lib/mozilla/plugins
RUN cp /download/flash/libflashplayer.so /usr/lib/mozilla/plugins

RUN cp -r -f /download/fonts /usr/local/share/
RUN fc-cache -f

RUN rm -rf /download/

ADD . /app