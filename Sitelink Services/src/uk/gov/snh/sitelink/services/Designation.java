package uk.gov.snh.sitelink.services;

enum Designation {
	SPA("Special Protection Area"),
	REGIONAL_PARK("Regional Park"),
	DEMO_MPA("Demonstration and Research MPA"),
	NSA("National Scenic Area"),
	NNR("National Nature Reserve"),
	MPA("Nature Conservation MPA"),
	COUNTRY_PARK("Country Park"),
	SSSI("Site of Special Scientific Interest"),
	SAC("Special Area of Conservation"),
	RAMSAR("Ramsar Site"),
	NATIONAL_PARK("National Park"),
	S49("Area covered by S.49 Agreement"),
	LNR("Local Nature Reserve"),
	UNKNOWN("Unknown")
    ;

    private final String designation;

    /**
     * @param designation
     */
    private Designation(final String designation) {
        this.designation = designation;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return designation;
    }
    
    public static Designation get(String designation) {
    	
    	for (Designation d : Designation.values()) {
    		if (d.toString().equals(designation))
    			return d;
    	}
    	
    	return UNKNOWN;
    }
}
