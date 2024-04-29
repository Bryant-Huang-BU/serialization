#!/bin/bash

# Create an array of allowed characters for the file names
declare -a chars=({a..z} {A..Z} {0..9} "_" "-" ".")

# Create 5 new files with random characters and valid names
for i in {1..5}; do
  # Generate a random file name with random length between 5 and 20 characters
  filename_len=$((RANDOM % 16 + 5))
  filename=""
  for j in {1..$filename_len}; do
    filename+="${chars[RANDOM % ${#chars[@]}]}"
  done

  # Generate a random valid first name and last name
  fname=$(rig -f -c 1 | awk '{print $1}')
  lname=$(rig -m -c 1 | awk '{print $1}')

  # Generate a random file extension with random length between 1 and 5 characters
  ext_len=$((RANDOM % 5 + 1))
  extension=""
  for j in {1..$ext_len}; do
    extension+="${chars[RANDOM % ${#chars[@]}]}"
  done

  # Generate a random file size between 10 and 100 bytes
  file_size=$((RANDOM % 91 + 10))

  # Create the new file with the random name, valid first name, valid last name, and extension
  echo -n -e "\0${file_size}" > "${filename}-${fname}-${lname}.${extension}"
done
