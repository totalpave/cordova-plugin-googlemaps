#!/usr/bin/env python3

#
# This script is replacing the functionality of <framework /> and simular tags in plugin.xml
# The reason why we are replacing these tags is because they are not properly installing libtilegen.xc
# The proper installation requires libtilegen.xcframework to be both embedded and linked with the binary.
# <framework /> is only capable 1 or the other.
#

try:
    from pbxproj import XcodeProject
    import sys

    project = XcodeProject.load(sys.argv[1] + '/platforms/ios/IRI Dev.xcodeproj/project.pbxproj')
    project.remove_files_by_path('IRI Dev/Plugins/cordova-plugin-googlemaps/libtilegen.xcframework')

    project.save()
except:
    print("cordova-plugin-googlemaps could not uninstall libtilegen.xcframework due to missing module pbxproj")
    