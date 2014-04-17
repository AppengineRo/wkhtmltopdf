#!/bin/sh
# first, installing dependencies
aptitude install openssl build-essential xorg libssl-dev

# for 64bits OS
wget http://wkhtmltopdf.googlecode.com/files/wkhtmltopdf-0.9.9-static-amd64.tar.bz2
tar xvjf wkhtmltopdf-0.9.9-static-amd64.tar.bz2
mv wkhtmltopdf-amd64 /usr/local/bin/wkhtmltopdf
chmod +x /usr/local/bin/wkhtmltopdf