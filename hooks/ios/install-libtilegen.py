#!/usr/bin/env python3

#
# This script is replacing the functionality of <framework /> and simular tags in plugin.xml
# The reason why we are replacing these tags is because they are not properly installing libtilegen.xcframework
# The proper installation requires libtilegen.xcframework to be both embedded and linked with the binary.
# <framework /> is only capable 1 or the other.
#

import importlib.util
try:
  from pbxproj import XcodeProject
  from pbxproj.pbxextensions.ProjectFiles import FileOptions
except:
  if input("pbxproj module not found. Can I install the module with \"sudo pip3 install pbxproj\"? [y/any other character] ") == "y":
    import os
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

# Copy xcframework file into the iOS platform
src = sys.argv[1] + '/plugins/cordova-plugin-googlemaps/src/ios/frameworks/libtilegen.xcframework'
dest = sys.argv[1] + '/platforms/ios/' + name + '/Plugins/cordova-plugin-googlemaps/libtilegen.xcframework'

shutil.copytree(src, dest)

# Add xcframework file to the XCode Project, ensure it is linked and embedded.
project = XcodeProject.load(sys.argv[1] + '/platforms/ios/' + name + '.xcodeproj/project.pbxproj')
file_options = FileOptions(embed_framework=True, code_sign_on_copy=True)
frameworks = project.get_or_create_group('Frameworks')
project.add_file(name + '/Plugins/cordova-plugin-googlemaps/libtilegen.xcframework', parent=frameworks, force=False, file_options=file_options)

project.save()
