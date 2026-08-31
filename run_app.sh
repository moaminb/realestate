#!/bin/bash
set -e

echo "🔨 در حال کامپایل برنامه اصلی با جاوا ۸..."
mkdir -p bin
javac --release 8 -d bin $(find src -name "*.java")

echo "🏢 در حال اجرای سامانه معاملات ملکی..."
java -cp bin Main
