
include "Referenced.thrift"

/**
 * Bikesheds are somewhat useful devices.
 */
struct Bikeshed {
  /** Primary colour of the bikeshed */
  1: Referenced.Colour primaryColour = Referenced.Colour.Red // Main colour - these are discarded.
  /** Secondary colour that was agreed as a compromise. */
  2: Referenced.Colour otherColour = Referenced.Colour.WHITE // Colour to be argued about.
}

struct StringDescription {
    /** Description above the field */
    1: required string (date_format = "yyyy_mm_dd") description (critical = "true")
}

union Description {
    1: StringDescription stringDescription
    /** Description of field */
    2: string poorlyDesignAPI (my_annotation = "true")
}

struct MyStruct {
    /** Id of the data */
    1: required i64 id
    /** Name (if missing means that it is a company account) */
    2: optional string name
    /** Account number in the form ACC_XXXXXX */
    3: required string accountNumber
    4: required Description description
    5: optional list<Bikeshed> bikeshed
    6: required map<i64, string> randomStrings
    7: optional set<i64> allowedNumbers
} (important_struct = "true")
