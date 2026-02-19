#!/usr/bin/env bash
# Start egov-mdms-service with this repo's mdms_data so IdFormat and Advocate masters are loaded.
# Run from digit_assignment root. Requires egov-mdms-service built in DIGIT-OSS.
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MDMS_DATA="$(cd "$SCRIPT_DIR/../mdms_data" && pwd)"
MDMS_JAR="${EGOV_MDMS_JAR:-}"
if [[ -z "$MDMS_JAR" ]]; then
  # Default: DIGIT-OSS sibling of digit_assignment (DIGIT-Backend/DIGIT-OSS, DIGIT-Backend/digit_assignment)
  DIGIT_OSS="$(cd "$SCRIPT_DIR/../.." && pwd)/DIGIT-OSS"
  if [[ -d "$DIGIT_OSS/core-services/egov-mdms-service/target" ]]; then
    JAR=$(find "$DIGIT_OSS/core-services/egov-mdms-service/target" -name "egov-mdms-service-*.jar" -not -name "*sources*" | head -1)
    if [[ -n "$JAR" ]]; then
      MDMS_JAR="$JAR"
    fi
  fi
fi
if [[ -z "$MDMS_JAR" || ! -f "$MDMS_JAR" ]]; then
  echo "egov-mdms-service JAR not found. Build it: cd DIGIT-OSS/core-services/egov-mdms-service && mvn clean package -DskipTests"
  echo "Or set EGOV_MDMS_JAR to the JAR path."
  exit 1
fi
export MDMS_CONF_PATH="$MDMS_DATA"
echo "Starting MDMS with MDMS_CONF_PATH=$MDMS_CONF_PATH"
exec java -jar "$MDMS_JAR"
