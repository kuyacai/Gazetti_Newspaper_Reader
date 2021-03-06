Gazetti
==============


An blazing fast Android app to make reading newspaper easy and interesting.
--------------

<a href="https://play.google.com/store/apps/details?id=in.sahildave.gazetti">
  <img alt="Get it on Google Play"
       src="http://developer.android.com/images/brand/en_generic_rgb_wo_60.png" />
</a>

This app is focused on giving full control to the users for what they want to read, down to the category level. From a Newspapers you'd like to read and select the category. If you don't like *Politics*, don't select it in your feed selector!

The latest news would be displayed in a list and the article would be displayed in a reading mode, free of external links and ads. You can now enjoy reading your newspapers distraction free.


Technology used
--------------

- **Parse.com as Backend** - To collect the latest news from newspapers periodically and maintaining it. Cloud code is written in JavaScript.
- **Jsoup** for parsing articles into "Reading Mode" - The app finds the top heading, top image and the article body from the links. It skips the unnecessary ads if within the body.
- **Gson** - To parse the user's feed preference into string and save it to sharedPrefs.
- **Picasso** - To load the images in article asynchronously.
- **Other Libraries Used** - ListViewAnimations, KenBurnsView (in Tablet layout), SmoothProgressBar, JazzyViewPager, TextJustify


License
--------------------

The MIT License (MIT)

Copyright (c) 2014 Sahil Dave

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

Follow:
-------------------------------

Sahil Dave <a href="https://twitter.com/sahildave1991" class="twitter-follow-button" data-show-count="false" data-size="large" data-dnt="true">Follow @sahildave1991</a>
<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+'://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);}}(document, 'script', 'twitter-wjs');</script>

Gazetti <a href="https://twitter.com/GazettiApp" class="twitter-follow-button" data-show-count="false" data-size="large" data-dnt="true">Follow @GazettiApp</a>
<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+'://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);}}(document, 'script', 'twitter-wjs');</script>
