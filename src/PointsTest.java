


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;


/**
 * Experimental class:
 * testing if it's possible to calculate the expectimax points 
 * in a somewhat "efficient" manner.
 * @author mbernt
 *
 */
public class PointsTest {
		final int maxPoints;
		final String letters;
		final byte[] setFreq;//the needed character for the moves
		final byte[] checkList;
		final ArrayList<Move> moves;
		final double probability;
		double correction=0.0;
		PointsTest winner=this;
		final ArrayList<PointsTest> losers=new ArrayList<PointsTest>();
		//boolean changed; used to know which should be used in calculation
		
		public PointsTest(final int _maxPoints,double _probability,final byte[] _setFreq,byte[] _checkList,ArrayList<Move> _moves,String _letters){
			maxPoints=_maxPoints;
			setFreq=_setFreq;
			moves=_moves;
			probability=_probability;
			checkList=_checkList;
			letters=_letters;
		}
		
		public boolean isSubsetTo(PointsTest rs){
			return Help.hasCharFreq2(checkList, setFreq, rs.setFreq, 0);
		}
		
//		private void tryToReplaceWinnerWithContestant(ArrayList<PointsTest> contestenders){
//			for(int i=0;i<contestenders.size();i++){
//				//use winner from previous levels in contest
//				PointsTest tmp=contestenders.get(i).winner;
//				if(tmp.isSubsetTo(this)){
//					if(tmp.maxPoints>winner.maxPoints){
//						//replace winner with the new winner
//						//if we get a rack with these letters we want to build the winner
//						if(winner!=this){
//							if(!losers.contains(tmp)){//maybe replace with hashSet
//								losers.add(winner);
//							}
//						}
//						winner=tmp;
//					} else {
//						if(!losers.contains(tmp)){//maybe replace with hashSet
//							losers.add(tmp);
//						}
//					}
//				}
//			}
//		}
		
		/**
		 * returns the winner, and adds losers to lose list.
		 * @param competitors
		 */
		private PointsTest competeAndReturnWinner(ArrayList<PointsTest> competitors){
			if(competitors.size()>0){
				for(PointsTest tmp:competitors){
					if(tmp.isSubsetTo(this)){
						if(tmp.maxPoints>winner.maxPoints){
							//replace winner with the new winner
							//if we get a rack with these letters we want to build the winner
							if(winner!=this){
								losers.add(winner);
							}
							winner=tmp;
						} else {
							losers.add(tmp);
						}
					}
				}
			}
			return winner;
		}
		
		/**
		 * returns the winner, and adds losers to lose list.
		 * @param winset
		 */
		private PointsTest competeAndReturnWinner(HashSet<PointsTest> winset){
			if(winset.size()>0){
				for(PointsTest tmp:winset){
					if(tmp.isSubsetTo(this)){
						if(tmp.maxPoints>winner.maxPoints){
							//replace winner with the new winner
							//if we get a rack with these letters we want to build the winner
							if(winner!=this){
								losers.add(winner);
							}
							winner=tmp;
						} else {
							losers.add(tmp);
						}
					}
				}
			}
			return winner;
		}
		
		/**
		 * the length parameter is to make it simpler to do
		 * 
		 * @param length
		 */
		private void adjustProbabilitiesIf(int length){
			if(letters.length()==length){
				for(int i=0;i<losers.size();i++){
					losers.get(i).correction-=probability+correction;
				}
			}
		}
		
		
	
//	/**
//	 * int index=nrNededLetters-1
//	 * all[index]
//	 * @param all
//	 */
//	private static void calculateWinners(ArrayList<PointsTest>[] all){
//		for(int length=0;length<all.length-1;length++){
//			ArrayList<PointsTest> nextList=all[length+1];
//			for(int index=0;index<nextList.size();index++){
//				//use list from this length as contenders against one at the next length
//				nextList.get(index).tryToReplaceWinnerWithContestant(all[length]);
//			}
//		}
//	}
	
	/**
	 * Untested.
	 * Gives the probability of choosing each move.
	 * Should only be run once.
	 * The time complexity is quadratic (or worse...).
	 * The space complexity might be to high...
	 * Creates a graph in a complicated way and goes backwards in the graph and adjusts
	 * the probabilities, so that they now (hopefully) are the probability to choose
	 * that move. Then you pretend that the choice is random and do the same calculation
	 * as in expectimax (for each move m: points+=m.probability*m.points).
	 * @param all
	 */
	public static void adjustProbabilities(ArrayList<PointsTest>[] all){
		HashSet<PointsTest> winset=new HashSet<PointsTest>(all[0]);
		ArrayList<PointsTest> competeSet=new ArrayList<PointsTest>(all[0]);
		
		
		ArrayList<PointsTest> newWinners=new ArrayList<PointsTest>();
		for(int i=1;i<all.length;i++){
			for(int j=0;j<all[i].size();j++){
				PointsTest tmp=all[i].get(j);
				//compete an returns a winner, this may be tmp
				newWinners.add(tmp.competeAndReturnWinner(competeSet));
			}
			//duplicates is removed because it's a set
			winset.addAll(newWinners);
			//get all new competitors, no duplicates because they were only added once
			competeSet.addAll(all[i]);
		}
		
		//adjust probabilities for the ones with maximum length first
		//in this way the probability won't be subtracted twice because 
		//it has already been subtracted from the one before
		for(int length=all.length;length>0;length--){
			for(PointsTest tmp:winset){
				tmp.adjustProbabilitiesIf(length);
			}
		}
	}
	
	
	
	public static ArrayList<PointsTest>[] all(ArrayList<Move> moves){
		//add each move with certain needed letters 
		HashMap<MoveWrapperTest, ArrayList<MoveWrapperTest>> map=new HashMap<MoveWrapperTest, ArrayList<MoveWrapperTest>>();
		for(Move m:moves){
//			MoveWrapper mv=new MoveWrapper(m);
//			ArrayList<MoveWrapper> list=map.get(mv);
//			if(list==null){
//				list=new ArrayList<MoveWrapper>();
//				map.put(mv, list);
//			}
		}
		@SuppressWarnings("unchecked")
		ArrayList<PointsTest>[] all=new ArrayList[7];
		//add to ArrayList<PointsTest>[] all
		//where the index in the array is neededLetters.length-1
		
		
		
		//return the resulting thing
		return all;
	}
	
	
	
	//highly experimental test code below
		/**
		 * untested, don't work
		 * @param unknownTiles
		 * @return
		 */
		public ArrayList<Long> rackProbabilities(final byte[] _unknownTiles, final byte max){
			//make a copy so that the values won't be changed
			byte[] unknownTiles=Arrays.copyOf(_unknownTiles, _unknownTiles.length);
			//remove so that there will be no duplicate combinations
			for(int i=0;i<unknownTiles.length;i++){
				if(unknownTiles[i]>max){
					unknownTiles[i]=max;
				}
			}
			//count the number of tiles in the pool
			int tilesLeft=0;
			for(int i=0;i<unknownTiles.length;i++){
				tilesLeft+=unknownTiles[i];
			}
			//construct list so that it's easier to get all
			//the combinations
			byte[] helper=new byte[tilesLeft];
			int index=0;
			for(int i=0;i<unknownTiles.length;i++){
				if(unknownTiles[i]>0){
					for(int j=0;j<unknownTiles[i];j++){
						helper[index]=(byte)i;
						index++;
					}
				}
			}
			int length=Math.min(7, tilesLeft);
			ArrayList<Long> res=new ArrayList<Long>();
			getCombinations(res,helper,0L,length-1, 0);
			return res;
		}
		
		/**
		 * count every subset from arr with length=depth-1.
		 * 
		 * Only works when length is less than 8.
		 * you should remove so that there are no more duplicates than depth+1
		 * of each number in input. 
		 * 
		 * O(arr.length over depth)
		 * 
		 * may be very slow.
		 * 
		 * @param res
		 * @param input
		 * @param holder
		 * @param depth
		 * @param start
		 */
		public void getCombinations(ArrayList<Long> res,final byte[] input,long holder,final int depth, final int start){
			if(depth==0){
				for(int i=start;i<input.length-depth;i++){
					//newHolder = holder|(input[i]<<(depth*8))
					res.add(holder|(input[i]<<(depth*8)));
				}
			} else {
				for(int i=start;i<input.length-depth;i++){
					//newHolder = holder|(input[i]<<(depth*8))
					getCombinations(res,input,holder|(input[i]<<(depth*8)),depth-1,i+1);
				}
			}
		}
		
		public String getRackFromHolder(final long holder,final int depth){
			StringBuilder sb=new StringBuilder(depth);
			for(int i=0;i<depth;i++){
				long tmp=holder>>(8*i);
				sb.append((char)(tmp+'a'));
			}
			return sb.toString();
		}
		
		
	
	
}
