#!/bin/bash

# Create an array of allowed characters for the file names and extensions
declare -a chars=({a..z} {A..Z} {0..9} "_" "-" ".")

# Create 5 new files with random characters, extensions, and sizes
for i in {1..20}; do
  # Generate a random file name with random length between 5 and 20 characters
  filename_len=$((RANDOM % 16 + 5))
  filename=""
  for j in {1..$filename_len}; do
    filename+="${chars[RANDOM % ${#chars[@]}]}"
  done

  # Generate a random file extension with random length between 1 and 5 characters
  ext_len=$((RANDOM % 5 + 1))
  extension=""
  for j in {1..$ext_len}; do
    extension+="${chars[RANDOM % ${#chars[@]}]}"
  done

  # Generate a random file size between 10 and 100 bytes
  file_size=$((RANDOM % 91 + 10))

  # Create the new file with the random name, extension, and size
  echo -n -e "\0${file_size}" > "${filename}.${extension}"
done
