#!/bin/sh
# A simple bash script, which is called from the Spigot Server before shutting down to reset the world.
# It is not possible to reset the worlds while the server is running, because the main world can never be unloaded.

# Create Output File
OUTPUT=worldReset.log

# Wait until the Server has shut down properly
echo "Waiting until the server has shut down properly" > $OUTPUT
sleep 15s

# Remove all files from the worlds:
#  - world
#  - world_nether
#  - world_the_end
WORLD="world"
NETHER="world_nether"
END="world_the_end"

echo "Starting with removing worlds:" >> $OUTPUT

# Removing Over-world
if [ -d $WORLD ]
then
  rm -r $WORLD
  echo " > ${WORLD} < was deleted!" >> $OUTPUT
else
  echo " > ${WORLD} < was already deleted!" >> $OUTPUT
fi

# Removing Nether
if [ -d $NETHER ]
then
  rm -r $NETHER
  echo " > ${NETHER} < was deleted!" >> $OUTPUT
else
  echo " > ${NETHER} < was already deleted!" >> $OUTPUT
fi

# Removing End
if [ -d $END ]
then
  rm -r $END
  echo " > ${END} < was deleted!" >> $OUTPUT
else
  echo " > ${END} < was already deleted!" >> $OUTPUT
fi

echo "All world were successfully deleted!" >> $OUTPUT


# Replace config value with a new Seed
echo "Generating Seed" >> $OUTPUT
VALUE=$(shuf -i 100000000000000000-999999999999999999 -n 1)
CONFIG_FILE="server.properties"
echo "Setting config Value" >>$OUTPUT
sed -i -e "s/\(level-seed*= *\).*/\1${VALUE}/" $CONFIG_FILE

# Starting Server Again!
echo "Starting Server" >> $OUTPUT
NAME="challenge"
echo "Starting screen called ${NAME} in detached mode..." >> $OUTPUT
screen -d -m -L -S ${NAME} bash -c "java -Xmx4000M -jar paper-1.19.3.jar"
