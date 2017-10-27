wget https://github.com/mozilla/geckodriver/releases/download/v0.18.0/geckodriver-v0.18.0-linux64.tar.gz
tar -xvzf geckodriver*
sudo mv geckodriver /usr/local/bin/
cd /usr/local/bin/
chmod +x geckodriver
export PATH=$PATH:/path-to-extracted-file/geckodriver