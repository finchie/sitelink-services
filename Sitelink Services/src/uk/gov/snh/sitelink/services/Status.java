package uk.gov.snh.sitelink.services;

enum Status {
	CURRENT("Current"),
	CONSULTATION("At consultation stage"),
	ADOPTED("SCI (adopted cSAC)"),
	CANDIDATE("Candidate SAC - submitted to EC"),
	UNKNOWN("Unknown")
    ;

    private final String status;

    /**
     * @param designation
     */
    private Status(final String status) {
        this.status = status;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return status;
    }
    
    public static Status get(String status) {
    	
    	for (Status d : Status.values()) {
    		if (d.toString().equals(status))
    			return d;
    	}
    	
    	return UNKNOWN;
    }
}
