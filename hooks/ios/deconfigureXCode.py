#!/usr/bin/env python3

# Configurations:
# Unset CXX Language Standard from C++ 17
# Remove Header Search path for libtilegen.xcframework's public headers.

import importlib.util
import os
try:
  from pbxproj import XcodeProject
  from pbxproj.pbxextensions.ProjectFiles import FileOptions
except:
  if input("pbxproj module not found. Can I install the module with \"sudo pip3 install pbxproj\"? [y/any other character] ") == "y":
    os.system('sudo pip3 install pbxproj')
    from pbxproj import XcodeProject
    from pbxproj.pbxextensions.ProjectFiles import FileOptions
  else:
    raise ModuleNotFoundError("pbxproj module not found and request to install module was denied.")

import sys
import shutil
import xml.etree.ElementTree as ET
tree = ET.parse(sys.argv[1] + '/config.xml')
root = tree.getroot()
name = root.find('{http://www.w3.org/ns/widgets}name').text

project = XcodeProject.load(sys.argv[1] + '/platforms/ios/' + name + '.xcodeproj/project.pbxproj')

project.remove_flags("CLANG_CXX_LANGUAGE_STANDARD", "c++17")
project.remove_header_search_paths([
  '${{inherited}}',
  '"' + name + '/Plugins/cordova-plugin-googlemaps/libtilegen.xcframework/ios-arm64/libtilegen.framework/Headers"'
])

project.save()
