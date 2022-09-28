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
except:
    if input("pbxproj module not found. Can I install the module with \"sudo pip3 install pbxproj\"? [y/any other character] ") == "y":
        import os
        os.system('sudo pip3 install pbxproj')
        from pbxproj import XcodeProject
    else:
        raise ModuleNotFoundError("pbxproj module not found and request to install module was denied.")

import sys
import xml.etree.ElementTree as ET
tree = ET.parse(sys.argv[1] + '/config.xml')
root = tree.getroot()
name = root.find('{http://www.w3.org/ns/widgets}name').text

project = XcodeProject.load(sys.argv[1] + '/platforms/ios/' + name + '.xcodeproj/project.pbxproj')
project.remove_files_by_path(name + '/Plugins/cordova-plugin-googlemaps/libtilegen.xcframework')

project.save()
