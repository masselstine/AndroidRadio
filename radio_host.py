from flask import Flask, request
import subprocess, signal, os
import urllib.request

app = Flask(__name__)

@app.route("/")
def hello():
    return "Hello World!"

@app.route("/stop")
def stop():
    cmd = "ps -A"
    p = subprocess.Popen(cmd.split(), stdout=subprocess.PIPE)
    out, err = p.communicate()

    out_str = out.decode()
    for line in out_str.splitlines():
        if "mpv" in line:
            pid = int(line.split(None, 1)[0])
            print("Killing %d" % pid)
            os.kill(pid, signal.SIGINT)
        
    return "Failed"

@app.route("/play/<path:url>")
def play_url(url):
    id = request.args.get('id', None)
    print("raw: %s?id=%s" % (url, id))
    data = "id=%s" % id
    contents = urllib.request.urlopen(url, data.encode()).read().decode()
    print("fetched: " + contents)
    cmd = "/usr/bin/mpv %s & disown" % contents.split('\n', 1)[0]
    print("command: " + cmd)
    #cmd = "/usr/bin/mpv %s & disown" % contents
    p = subprocess.call(cmd, shell=True)
    #subprocess.Popen(['/usr/bin/mpv', contents.split('\n', 1)[0]], shell=True, stdin=None, stdout=None, stderr=None, close_fds=True)
    return "Done"


if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0")
