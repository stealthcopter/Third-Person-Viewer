# Third-Person-Viewer

Take two phones and use one of them to transmit video to the other. Wearing the receiving phone in a VR headset this gives the user an out of body experience that is both fun and disorientating.

# Features
+ Simple ip socket to send video frames
+ One app handles both server and VR setup

# TODO
+ Ability to change video quality
+ Automatically find ip address from a broadcast
+ Add switching of cameras if multiple exist
+ Use cardboard/daydream apis

# Limitations
+ Code is shit, but it works, will improve later.
+ Strong wifi or hotspot connection required to ensure fast framerate
+ Doesn't use daydream or cardboard api currently (was written in a day and is non-standard, I'm sorry!), disable NFC if you're putting it in your daydream viewer to stop it taking over the screen.
+ JPEG stream seems stupid way of doing it, but video encoding introduces too much latency (any suggestions I'd love to hear about)

## Building

It's a standard gradle project.

# Contributing

I welcome pull requests, issues and feedback.

- Fork it
- Create your feature branch (git checkout -b my-new-feature)
- Commit your changes (git commit -am 'Added some feature')
- Push to the branch (git push origin my-new-feature)
- Create new Pull Request

