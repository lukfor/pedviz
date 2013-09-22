package pedviz.clustering.clique.calc;

public class Constants {
	
	public static final String SEED  = "1234";
	public static final String PERMUTATIONS  = "1000";
	
	
	public static final String SQLPEDIGREE  = "Select distinct famid,id,idfather as father,idmother as mother,sex,affection as Sel,genotyped as Gen FROM pedigree";
	/*
	 * 
	 * TODO
	 */
	public static final String SQLGROUPS_C    = "Select distinct setid as CID, no, mean, stdev, _min , _max FROM groupstat ";
	public static final String SQLGROUPS_R    = "Select distinct setid as CID, no, mean, stdev, _min , _max FROM residualstat ";
	public static final String SQLGROUPS_E    = "Select distinct kinship as from_X_to_1 ,setid as CID, no, mean, stdev, _min , _max FROM egroupstat ";

	public static final String SQLGROUPS_WHERE_K    = " where type = 'K'";
	public static final String SQLGROUPS_WHERE_I    = " where type = 'I'";
	
	public static final String SQLGROUPS_P   = "Select distinct famId as PID, dim as no,gen,bits,stepanc AS Step  FROM buildinfo";
	
	public static final String SQLRESIDUALS = "Select distinct id,idfather as father,idmother as mother,sex,affection as Sel,genotyped as GENO FROM pedigree where id not in (Select id from groups) and id not in (Select id from residuals) and affection = 2";
	public static final String SQLRESIDUALSID = "Select distinct id FROM pedigree where id not in (Select id from groups) and id not in (Select id from residuals) and affection = 2";
}
